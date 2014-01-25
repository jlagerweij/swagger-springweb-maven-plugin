package com.moreapps.swagger;

import java.util.List;

public class ServiceOperations {
    private String path;
    private String description;
    private List<ServiceOperation> operations;
    private List<String> responseMessages;
    private boolean deprecated;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ServiceOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<ServiceOperation> operations) {
        this.operations = operations;
    }

    public List<String> getResponseMessages() {
        return responseMessages;
    }

    public void setResponseMessages(List<String> responseMessages) {
        this.responseMessages = responseMessages;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
}
