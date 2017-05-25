package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;

public class UploadMemberDTO extends MemberDTO {

    @Expose
    private VisitDTO visit;

    public UploadMemberDTO(String id, String name, String address, String phone, String qrcode, VisitDTO visit) {
        super(id, name, address, phone, qrcode);
        this.visit = visit;
    }

    public VisitDTO getVisit() {
        return visit;
    }
}
