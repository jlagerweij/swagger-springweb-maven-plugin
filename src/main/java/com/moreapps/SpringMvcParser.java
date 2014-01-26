package com.moreapps;

import com.google.common.collect.Lists;
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
import com.wordnik.swagger.core.SwaggerSpec;

import org.reflections.Reflections;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

public class SpringMvcParser {

    private String apiVersion;
    private String basePath;

    public Service parse(String... controllerPackages) {
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        for (String controllerPackage : controllerPackages) {
            Reflections reflections = new Reflections(controllerPackage);
            controllerClasses.addAll(reflections.getTypesAnnotatedWith(Controller.class));
        }

        Service service = new Service();
        service.setApiVersion(apiVersion);
        service.setSwaggerVersion(SwaggerSpec.version());
        service.setApis(new ArrayList<ServiceApi>());

        addControllersAsServices(controllerClasses, service);

        System.out.println(format("Found %d services.", service.getApis().size()));

        return service;
    }

    private void addControllersAsServices(Set<Class<?>> controllerClasses, Service service) {
        int position = 0;
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
            serviceApi.setPosition(position++);

            RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String[] value = requestMapping.value();
                if (value.length > 0) {
                    serviceApi.setPath(format("%s.{format}", value[0]));
                    serviceApi.getDetails().setResourcePath(value[0]);
                }
                details.getProduces().addAll(Lists.newArrayList(requestMapping.produces()));
                details.getConsumes().addAll(Lists.newArrayList(requestMapping.consumes()));
            }

            Api api = controllerClass.getAnnotation(Api.class);
            if (api != null) {
                if (!StringUtils.isEmpty(api.value())) {
                    serviceApi.setPath(api.value());
                }
                serviceApi.setDescription(api.description());
                serviceApi.getDetails().setDescription(api.description());
            }

            System.out.println(serviceApi.getPath());

            addMethodsAsOperations(controllerClass, serviceApi);

            service.getApis().add(serviceApi);
        }
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

            if (requestMapping.value().length > 0) {
                serviceOperations.setPath(format("%s%s", serviceApi.getDetails().getResourcePath(), requestMapping.value()[0]));
            } else {
                serviceOperations.setPath(serviceApi.getDetails().getResourcePath());
            }
            operation.setMethod(requestMapping.method()[0].name());
            if (Collection.class.isAssignableFrom(method.getReturnType())) {
                operation.setResponseClass("List[" + method.getGenericReturnType().getClass().getName() + "]");
            } else {
                operation.setResponseClass(method.getReturnType().getName());
            }
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

            System.out.println(format("%10s %s", operation.getMethod(), serviceOperations.getPath()));

            addClassToModel(serviceApi, method.getReturnType(), method.getGenericReturnType());

            // Detect parameters
            operation.setParameters(new ArrayList<ServiceOperationParameter>());
            Class<?>[] parameterTypes = method.getParameterTypes();
            String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
            for (int i = 0; i < parameterTypes.length; i++) {
                ServiceOperationParameter parameter = new ServiceOperationParameter();
                parameter.setName(parameterNames[i]);
                parameter.setDataType(parameterTypes[i].getName());
                operation.getParameters().add(parameter);

                addClassToModel(serviceApi, parameterTypes[i], null);
            }

            serviceOperations.getOperations().add(operation);
            serviceApi.getDetails().getApis().add(serviceOperations);
        }
    }

    private void addClassToModel(ServiceApi serviceApi, Class<?> clazz, Type genericReturnType) {
        if (genericReturnType instanceof ParameterizedType) {
            clazz = (Class<?>) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
        }
        if (String.class.isAssignableFrom(clazz)
                || Collection.class.isAssignableFrom(clazz)) {
            return;
        }
        if (!serviceApi.getDetails().getModels().containsKey(clazz.getName())) {
            ServiceModel model = new ServiceModel();
            model.setId(clazz.getName());
            model.setName(clazz.getName());
            model.setQualifiedType(clazz.getName());
            HashMap<String, ServiceModelProperty> properties = new HashMap<String, ServiceModelProperty>();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ServiceModelProperty property = new ServiceModelProperty();
                property.setQualifiedType(field.getType().getName());
                property.setType(field.getType().getName());
                properties.put(field.getName(), property);
                addClassToModel(serviceApi, field.getType(), field.getGenericType());
            }
            model.setProperties(properties);
            serviceApi.getDetails().getModels().put(clazz.getName(), model);
        }
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
