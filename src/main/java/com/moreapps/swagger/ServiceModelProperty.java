package com.moreapps.swagger;

import java.util.Map;

public class ServiceModelProperty {
    private String type;
    private String qualifiedType;
    private int position;
    private boolean required;
    private String description;
    private AllowableValues allowableValues;
    private Map<String, String> items;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQualifiedType() {
        return qualifiedType;
    }

    public void setQualifiedType(String qualifiedType) {
        this.qualifiedType = qualifiedType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AllowableValues getAllowableValues() {
        return allowableValues;
    }

    public void setAllowableValues(AllowableValues allowableValues) {
        this.allowableValues = allowableValues;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }
}
