package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadResponse {
    @Expose
    private String info;
    @Expose
    @SerializedName("new_users")
    private List<ExternalIdPair> externalIdPairs;
    @Expose
    @SerializedName("new_docs")
    private List<UploadedDocument> uploadedDocuments;

    public UploadResponse() {
    }

    public List<UploadedDocument> getUploadedDocuments() {
        return uploadedDocuments;
    }

    public List<ExternalIdPair> getExternalIdPairs() {
        return externalIdPairs;
    }

    public void setExternalIdPairs(List<ExternalIdPair> externalIdPairs) {
        this.externalIdPairs = externalIdPairs;
    }
}
