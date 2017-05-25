package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DownloadTrackDTO extends TrackDTO {

    @Expose
    List<DownloadMemberDTO> members;
    @Expose
    @SerializedName("service_codes")
    private List<ServiceCodeDTO> serviceCodes;
    @Expose
    @SerializedName("document_codes")
    private List<DocumentCodeDTO> documentCodes;


    public DownloadTrackDTO(String id, String name, String date, List<DownloadMemberDTO> members,
                            List<ServiceCodeDTO> serviceCodes, List<DocumentCodeDTO> documentCodes) {
        super(id, name, date);
        this.members = members;
        this.serviceCodes = serviceCodes;
        this.documentCodes = documentCodes;
    }

    public List<DownloadMemberDTO> getMembers() {
        return members;
    }

    public List<ServiceCodeDTO> getServiceCodes() {
        return serviceCodes;
    }

    public List<DocumentCodeDTO> getDocumentCodes() {
        return documentCodes;
    }
}
