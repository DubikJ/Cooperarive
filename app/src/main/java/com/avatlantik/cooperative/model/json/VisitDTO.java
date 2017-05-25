package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public final class VisitDTO {
    @Expose
    private Date date;
    @Expose
    @SerializedName("milk_params")
    private MilkParamDTO milkParam;
    @Expose
    private List<ServiceDTO> services;
    @Expose
    private List<DocumentDTO> documents;

    public VisitDTO(Date date, MilkParamDTO milkParam, List<ServiceDTO> services, List<DocumentDTO> documents) {
        this.date = date;
        this.milkParam = milkParam;
        this.services = services;
        this.documents = documents;
    }

    public Date getDate() {
        return date;
    }

    public MilkParamDTO getMilkParam() {
        return milkParam;
    }

    public List<ServiceDTO> getServices() {
        return services;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }
}
