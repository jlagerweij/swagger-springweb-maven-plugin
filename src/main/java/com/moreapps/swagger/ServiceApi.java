package com.moreapps.swagger;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ServiceApi {
    private String path;
    private String description;
    private int position;

    @JsonIgnore
    private ServiceApiDetail details;

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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setDetails(ServiceApiDetail details) {
        this.details = details;
    }

    public ServiceApiDetail getDetails() {
        return details;
    }
}
