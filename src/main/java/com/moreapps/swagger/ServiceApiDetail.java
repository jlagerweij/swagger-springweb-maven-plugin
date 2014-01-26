package com.moreapps.swagger;

import java.util.List;
import java.util.Map;

public class ServiceApiDetail {
    private String apiVersion;
    private String swaggerVersion;
    private String basePath;
    private String resourcePath;
    private List<String> produces;
    private List<String> consumes;
    private List<String> protocols;
    private List<String> authorizations;
    private List<ServiceOperations> apis;
    private Map<String, ServiceModel> models;
    private String description;
    private int position;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }

    public void setSwaggerVersion(String swaggerVersion) {
        this.swaggerVersion = swaggerVersion;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public List<String> getProduces() {
        return produces;
    }

    public void setProduces(List<String> produces) {
        this.produces = produces;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<String> consumes) {
        this.consumes = consumes;
    }

    public List<String> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<String> protocols) {
        this.protocols = protocols;
    }

    public List<String> getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(List<String> authorizations) {
        this.authorizations = authorizations;
    }

    public List<ServiceOperations> getApis() {
        return apis;
    }

    public void setApis(List<ServiceOperations> apis) {
        this.apis = apis;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Map<String, ServiceModel> getModels() {
        return models;
    }

    public void setModels(Map<String, ServiceModel> models) {
        this.models = models;
    }
}
