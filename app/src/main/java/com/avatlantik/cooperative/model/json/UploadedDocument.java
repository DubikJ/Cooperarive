package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;

public class UploadedDocument {
    @Expose
    private String name;
    @Expose
    private String filename;

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }
}
