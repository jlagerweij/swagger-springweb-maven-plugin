package com.moreapps;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Lists;
import com.moreapps.swagger.AllowableValues;
import com.moreapps.swagger.Service;
import com.moreapps.swagger.ServiceApi;
import com.moreapps.swagger.ServiceApiDetail;
import com.moreapps.swagger.ServiceModel;
import com.moreapps.swagger.ServiceModelProperty;
import com.moreapps.swagger.ServiceOperation;
import com.moreapps.swagger.ServiceOperationParameter;
import com.moreapps.swagger.ServiceOperations;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import org.apache.maven.plugin.logging.Log;
import org.reflections.Reflections;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.moreapps.SwaggerUtils.asPath;
import static com.moreapps.SwaggerUtils.getSwaggerTypeFor;
import static com.moreapps.SwaggerUtils.isSwaggerPrimitive;
import static java.lang.String.format;

public class SpringMvcParser {
    private static final String SWAGGER_SPEC_VERSION = "1.2";
    private final Log log;

    private String apiVersion;
    private String basePath;

    public SpringMvcParser(Log log) {
        this.log = log;
    }
    
    private Log getLog() {
        return log;
    }

    public Service parse(String... controllerPackages) {
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        for (String controllerPackage : controllerPackages) {
            getLog().info("Controller packages: " + controllerPackage);
            Reflections reflections = new Reflections(controllerPackage);
            controllerClasses.addAll(reflections.getTypesAnnotatedWith(Controller.class));
        }

        Service service = new Service();
        service.setApiVersion(apiVersion);
        service.setSwaggerVersion(SWAGGER_SPEC_VERSION);
        service.setBasePath(basePath);
        service.setApis(new ArrayList<ServiceApi>());

        addControllersAsServices(controllerClasses, service);

        sortServicesAlphabetically(service);

        simplifyModel(service);

        getLog().info(format("Found %d services.", service.getApis().size()));

        return service;
    }

    private void simplifyModel(Service service) {
        getLog().info("Simplifing model.");
        ModelSimplifier modelSimplifier = new ModelSimplifier(getLog());
        for (ServiceApi serviceApi : service.getApis()) {

            Map<String, ServiceModel> newModels = new HashMap<String, ServiceModel>();
            Map<String, ServiceModel> models = serviceApi.getDetails().getModels();
            for (String key : models.keySet()) {

                ServiceModel serviceModel = models.get(key);
                serviceModel.setId(modelSimplifier.simplify(serviceModel.getId()));
                serviceModel.setName(modelSimplifier.simplify(serviceModel.getName()));
                serviceModel.setQualifiedType(modelSimplifier.simplify(serviceModel.getQualifiedType()));

                if (serviceModel.getSubTypes() != null) {
                    List<String> newSubTypes = new ArrayList<String>();
                    for (String subType : serviceModel.getSubTypes()) {
                        newSubTypes.add(modelSimplifier.simplify(subType));
                    }
                    serviceModel.setSubTypes(newSubTypes);
                }

                Map<String, ServiceModelProperty> properties = serviceModel.getProperties();
                for (String propertyKey : properties.keySet()) {
                    ServiceModelProperty serviceModelProperty = properties.get(propertyKey);

                    serviceModelProperty.setType(modelSimplifier.simplify(serviceModelProperty.getType()));
                    serviceModelProperty.setQualifiedType(modelSimplifier.simplify(serviceModelProperty.getQualifiedType()));

                    if (serviceModelProperty.getItems() != null) {
                        Map<String, String> newItems = new HashMap<String, String>();
                        for (String itemKey : serviceModelProperty.getItems().keySet()) {
                            String itemValue = serviceModelProperty.getItems().get(itemKey);
                            if (itemKey.equals("$ref")) {

                                newItems.put(itemKey, modelSimplifier.simplify(itemValue));
                            } else {
                                newItems.put(itemKey, itemValue);
                            }
                        }
                        serviceModelProperty.setItems(newItems);
                    }
                }

                newModels.put(modelSimplifier.simplify(key), serviceModel);
            }
            for (ServiceOperations serviceOperations : serviceApi.getDetails().getApis()) {
                for (ServiceOperation serviceOperation : serviceOperations.getOperations()) {

                    serviceOperation.setResponseClass(modelSimplifier.simplify(serviceOperation.getResponseClass()));

                    for (ServiceOperationParameter serviceOperationParameter : serviceOperation.getParameters()) {
                        serviceOperationParameter.setDataType(modelSimplifier.simplify(serviceOperationParameter.getDataType()));
                    }
                }
            }

            serviceApi.getDetails().setModels(newModels);
        }
    }



    private void sortServicesAlphabetically(Service service) {
        Collections.sort(service.getApis(), new Comparator<ServiceApi>() {
            @Override
            public int compare(ServiceApi o1, ServiceApi o2) {
                return o1.getPath().compareTo(o2.getPath());
            }
        });
        int position = 0;
        for (ServiceApi serviceApi : service.getApis()) {
            serviceApi.setPosition(position++);
        }
    }

    private void addControllersAsServices(Set<Class<?>> controllerClasses, Service service) {
        for (Class<?> controllerClass : controllerClasses) {
            ServiceApi serviceApi = new ServiceApi();

            ServiceApiDetail details = new ServiceApiDetail();
            details.setBasePath(basePath);
            details.setApis(new ArrayList<ServiceOperations>());
            details.setProduces(new ArrayList<String>());
            details.setConsumes(new ArrayList<String>());
            details.setProtocols(new ArrayList<String>());
            details.setAuthorizations(new ArrayList<String>());
            details.setModels(new HashMap<String, ServiceModel>());
            serviceApi.setDetails(details);

            serviceApi.getDetails().setApiVersion(service.getApiVersion());
            serviceApi.getDetails().setSwaggerVersion(service.getSwaggerVersion());

            RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String[] value = requestMapping.value();
                if (value.length > 0) {
                    serviceApi.setPath(sanitizePath(format("%s.{format}", asPath(value[0]))));
                    serviceApi.getDetails().setResourcePath(sanitizePath(asPath(value[0])));
                }
                details.getProduces().addAll(Lists.newArrayList(requestMapping.produces()));
                details.getConsumes().addAll(Lists.newArrayList(requestMapping.consumes()));
            }

            Api api = controllerClass.getAnnotation(Api.class);
            if (api != null) {
                if (!StringUtils.isEmpty(api.value())) {
                    serviceApi.setPath(sanitizePath(format("%s.{format}", asPath(api.value()))));
                    serviceApi.getDetails().setResourcePath(sanitizePath(asPath(api.value())));
                }
                serviceApi.setDescription(api.description());
                serviceApi.getDetails().setDescription(api.description());
            }

            if (requestMapping == null && api == null) {
                continue;
            }

            getLog().info(serviceApi.getPath());

            addMethodsAsOperations(controllerClass, serviceApi);

            service.getApis().add(serviceApi);
        }
    }

    private String sanitizePath(String path) {
        return path.replace("?", "_");
    }

    private void addMethodsAsOperations(Class<?> controllerClass, ServiceApi serviceApi) {
        int position = 0;
        for (Method method : controllerClass.getMethods()) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping == null) {
                continue;
            }

            ServiceOperations serviceOperations = new ServiceOperations();
            serviceOperations.setDescription(serviceApi.getDescription());
            serviceOperations.setOperations(new ArrayList<ServiceOperation>());
            serviceOperations.setResponseMessages(new ArrayList<String>());

            ServiceOperation operation = new ServiceOperation();
            operation.setPosition(position++);
            operation.setProduces(new ArrayList<String>());
            operation.setConsumes(new ArrayList<String>());
            operation.setProtocols(new ArrayList<String>());
            operation.setAuthorizations(new ArrayList<String>());
            operation.setNickname(method.getName());

            if (requestMapping.value().length > 0) {
                serviceOperations.setPath(requestMapping.value()[0]);
            } else {
                serviceOperations.setPath(serviceApi.getDetails().getResourcePath());
            }
            operation.setMethod(requestMapping.method()[0].name());

            operation.setResponseClass(getClassFrom(serviceApi, method.getReturnType(), method.getGenericReturnType()));
            operation.getProduces().addAll(Lists.newArrayList(requestMapping.produces()));
            operation.getConsumes().addAll(Lists.newArrayList(requestMapping.consumes()));

            ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
            if (apiOperation != null) {
                operation.setSummary(apiOperation.value());
                if (!apiOperation.produces().isEmpty()) {
                    operation.getProduces().addAll(Lists.newArrayList(apiOperation.produces()));
                }
                if (!apiOperation.consumes().isEmpty()) {
                    operation.getConsumes().addAll(Lists.newArrayList(apiOperation.consumes()));
                }
            }

            getLog().info(format("%10s %s", operation.getMethod(), serviceOperations.getPath()));

            // Detect parameters
            operation.setParameters(new ArrayList<ServiceOperationParameter>());
            Class<?>[] parameterTypes = method.getParameterTypes();
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
            for (int i = 0; i < parameterTypes.length; i++) {
                if (HttpServletResponse.class.isAssignableFrom(parameterTypes[i])) {
                    continue;
                }
                ServiceOperationParameter parameter = new ServiceOperationParameter();
                parameter.setName(parameterNames[i]);
                operation.getParameters().add(parameter);

                parameter.setDataType(getClassFrom(serviceApi, parameterTypes[i], genericParameterTypes[i]));
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof PathVariable) {
                        parameter.setParamType("path");
                    }
                    if (annotation instanceof RequestBody) {
                        parameter.setParamType("body");
                    }
                }
            }

            serviceOperations.getOperations().add(operation);
            serviceApi.getDetails().getApis().add(serviceOperations);
        }
    }

    private void addClassToModel(ServiceApi serviceApi, Type actualTypeArgument) {
        if (actualTypeArgument instanceof Class) {
            addClassToModel(serviceApi, (Class<?>) actualTypeArgument);
        }
    }

    private void addClassToModel(ServiceApi serviceApi, Class<?> clazz) {
        if (HttpServletRequest.class.isAssignableFrom(clazz)
                || HttpServletResponse.class.isAssignableFrom(clazz)) {
            return;
        }
        if (!isSwaggerPrimitive(clazz) && !serviceApi.getDetails().getModels().containsKey(clazz.getName())) {
            getLog().info(" Modelclass: " + clazz.getName());
            ServiceModel model = new ServiceModel();
            model.setId(clazz.getName());
            model.setName(clazz.getName());
            model.setQualifiedType(clazz.getName());

            JsonTypeInfo jsonTypeInfo = clazz.getAnnotation(JsonTypeInfo.class);
            JsonSubTypes jsonSubTypes = clazz.getAnnotation(JsonSubTypes.class);
            if (jsonTypeInfo != null && jsonSubTypes != null) {
                model.setDiscriminator(jsonTypeInfo.property());
                List<String> subTypes = new ArrayList<String>();
                for (JsonSubTypes.Type type : jsonSubTypes.value()) {
                    subTypes.add(type.value().getName());
                }
                model.setSubTypes(subTypes);
            }
            Map<Class<?>, Type> moreClassesToAdd = new HashMap<Class<?>, Type>();
            HashMap<String, ServiceModelProperty> properties = new HashMap<String, ServiceModelProperty>();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ServiceModelProperty property = new ServiceModelProperty();
                if (Enum.class.isAssignableFrom(field.getType())) {
                    property.setType("string");
                    AllowableValues allowableValues = new AllowableValues();
                    allowableValues.setValueType("LIST");
                    List<String> values = new ArrayList<String>();
                    for (Object o : field.getType().getEnumConstants()) {
                        values.add(o.toString());
                    }
                    allowableValues.setValues(values);
                    property.setAllowableValues(allowableValues);
                    properties.put(field.getName(), property);
                } else {
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        Type[] actualTypeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                        property.setType("array");
                        Map<String, String> items = new HashMap<String, String>();
                        items.put("$ref", getSwaggerTypeFor(actualTypeArguments[0]));
                        property.setItems(items);
                        properties.put(field.getName(), property);
                    } else {
                        String className = getClassFrom(serviceApi, field.getType(), field.getGenericType());
                        property.setQualifiedType(getSwaggerTypeFor(field.getType()));
                        property.setType(className);
                        properties.put(field.getName(), property);
                    }

                    moreClassesToAdd.put(field.getType(), field.getGenericType());
                }
            }
            model.setProperties(properties);
            serviceApi.getDetails().getModels().put(clazz.getName(), model);

            for (Class<?> classToAdd : moreClassesToAdd.keySet()) {
                Type genericType = moreClassesToAdd.get(classToAdd);
                getClassFrom(serviceApi, classToAdd, genericType);
            }
        }
    }

    private String getClassFrom(ServiceApi serviceApi, Type type, Type genericReturnType) {
        if (Void.class.isAssignableFrom((Class<?>) type)) {
            return "void";
        } else if (Collection.class.isAssignableFrom((Class<?>) type)) {
            Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            addClassToModel(serviceApi, actualTypeArguments[0]);
            return format("List[%s]", getSwaggerTypeFor(actualTypeArguments[0]));
        } else if (Map.class.isAssignableFrom((Class<?>) type)) {
            Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            addClassToModel(serviceApi, actualTypeArguments[0]);
            addClassToModel(serviceApi, actualTypeArguments[1]);
            return format("Map[%s,%s]", getSwaggerTypeFor(actualTypeArguments[0]), getSwaggerTypeFor(actualTypeArguments[1]));
        } else if (ResponseEntity.class.isAssignableFrom((Class<?>) type)) {
            Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            addClassToModel(serviceApi, actualTypeArguments[0]);
            return format("ResponseEntity[%s]", getSwaggerTypeFor(actualTypeArguments[0]));
        } else {
            addClassToModel(serviceApi, type);
            return getSwaggerTypeFor(type);
        }
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
