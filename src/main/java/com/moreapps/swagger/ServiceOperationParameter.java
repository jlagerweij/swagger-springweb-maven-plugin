package com.moreapps.swagger;

import java.util.List;

public class ServiceOperationParameter {
    private String name;
    private String description;
    private String defaultValue;
    private boolean required;
    private boolean allowMultiple;
    private String dataType;
    private List<String> allowableValues;
    private String paramType;
    private String paramAccess;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<String> getAllowableValues() {
        return allowableValues;
    }

    public void setAllowableValues(List<String> allowableValues) {
        this.allowableValues = allowableValues;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamAccess() {
        return paramAccess;
    }

    public void setParamAccess(String paramAccess) {
        this.paramAccess = paramAccess;
    }
}
