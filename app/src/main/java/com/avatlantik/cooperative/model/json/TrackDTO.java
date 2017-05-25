package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;

public class TrackDTO {
    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String date;

    public TrackDTO(String id, String name, String date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

}
