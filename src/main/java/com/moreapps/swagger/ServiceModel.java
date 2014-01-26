package com.moreapps.swagger;

import java.util.List;
import java.util.Map;

public class ServiceModel {
    private String id;
    private String name;
    private String qualifiedType;
    private Map<String, ServiceModelProperty> properties;
    private String description;
    private String baseModel;
    private String discriminator;
    private List<String> subTypes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualifiedType() {
        return qualifiedType;
    }

    public void setQualifiedType(String qualifiedType) {
        this.qualifiedType = qualifiedType;
    }

    public Map<String, ServiceModelProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, ServiceModelProperty> properties) {
        this.properties = properties;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseModel() {
        return baseModel;
    }

    public void setBaseModel(String baseModel) {
        this.baseModel = baseModel;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public List<String> getSubTypes() {
        return subTypes;
    }

    public void setSubTypes(List<String> subTypes) {
        this.subTypes = subTypes;
    }
}
