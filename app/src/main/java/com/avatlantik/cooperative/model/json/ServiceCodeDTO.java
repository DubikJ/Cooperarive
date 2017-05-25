package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;

public final class ServiceCodeDTO {
    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String parentid;

    public ServiceCodeDTO(String id, String name, String parentid) {
        this.id = id;
        this.name = name;
        this.parentid = parentid;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String parentId() {
        return parentid;
    }
}
