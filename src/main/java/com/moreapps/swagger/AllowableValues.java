package com.moreapps.swagger;

import java.util.List;

public class AllowableValues {
    private String valueType;
    private List<String> values;

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
