package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;

public final class DocumentCodeDTO {
    @Expose
    private String id;
    @Expose
    private String name;

    public DocumentCodeDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
