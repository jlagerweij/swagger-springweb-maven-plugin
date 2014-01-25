package com.moreapps.swagger;

import java.util.List;

public class Service {
    private String apiVersion;
    private String swaggerVersion;
    private List<ServiceApi> apis;
    private List<String> authorizations;
    private ServiceInfo info;

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

    public List<ServiceApi> getApis() {
        return apis;
    }

    public void setApis(List<ServiceApi> apis) {
        this.apis = apis;
    }

    public List<String> getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(List<String> authorizations) {
        this.authorizations = authorizations;
    }

    public ServiceInfo getInfo() {
        return info;
    }

    public void setInfo(ServiceInfo info) {
        this.info = info;
    }
}
