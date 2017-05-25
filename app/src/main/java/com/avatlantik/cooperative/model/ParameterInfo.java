package com.avatlantik.cooperative.model;

import java.io.Serializable;

public class ParameterInfo implements Serializable {
    private String name;
    private String value;
    private boolean editable;

    public ParameterInfo(String name, String value, boolean editable) {
        this.name = name;
        this.value = value;
        this.editable = editable;
    }

    public ParameterInfo(String name, String value) {
        this.name = name;
        this.value = value;
        this.editable = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
