package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;

public final class ServiceDTO {
    @Expose
    private String code;
    @Expose
    private boolean value;

    public ServiceDTO(String code, boolean value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public boolean getValue() {
        return value;
    }
}
