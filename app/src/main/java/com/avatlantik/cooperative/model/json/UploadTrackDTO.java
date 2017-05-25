package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;

import java.util.List;

public class UploadTrackDTO extends TrackDTO {

    @Expose
    private List<UploadMemberDTO> members;

    public UploadTrackDTO(String id, String name, String date, List<UploadMemberDTO> members) {
        super(id, name, date);
        this.members = members;
    }

    public List<UploadMemberDTO> getMembers() {
        return members;
    }
}
