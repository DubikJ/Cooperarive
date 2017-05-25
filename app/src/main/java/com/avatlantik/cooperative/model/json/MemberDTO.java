package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberDTO {
    @Expose
    @SerializedName("external_id")
    private String externalId;
    @Expose
    private String name;
    @Expose
    private String address;
    @Expose
    private String phone;
    @Expose
    private String qrcode;

    public MemberDTO(String externalId, String name, String address, String phone, String qrcode) {
        this.externalId = externalId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.qrcode = qrcode;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getQrcode() {
        return qrcode;
    }

}
