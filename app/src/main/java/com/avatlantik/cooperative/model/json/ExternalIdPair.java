package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExternalIdPair {

    @Expose
    @SerializedName("app_external_id")
    private String appExternalId;
    @Expose
    @SerializedName("new_external_id")
    private String newExternalId;

    public ExternalIdPair(String appExternalId, String newExternalId) {
        this.appExternalId = appExternalId;
        this.newExternalId = newExternalId;
    }

    public String getAppExternalId() {
        return appExternalId;
    }

    public void setAppExternalId(String appExternalId) {
        this.appExternalId = appExternalId;
    }

    public String getNewExternalId() {
        return newExternalId;
    }

    public void setNewExternalId(String newExternalId) {
        this.newExternalId = newExternalId;
    }
}
