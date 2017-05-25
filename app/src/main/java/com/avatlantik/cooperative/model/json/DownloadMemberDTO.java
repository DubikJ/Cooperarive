package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DownloadMemberDTO extends MemberDTO {

    @Expose
    private List<ServiceDTO> services;
    @Expose
    private List<DocumentDTO> documents;
    @Expose
    @SerializedName("member_stats")
    private MemberStatsDTO memberStats;
    @Expose
    @SerializedName("milk_stats")
    private MilkStatsDTO milkStats;

    public DownloadMemberDTO(String id, String name, String address, String phone, String qrcode,
                             List<ServiceDTO> services, List<DocumentDTO> documents,
                             MemberStatsDTO memberStats, MilkStatsDTO milkStats) {
        super(id, name, address, phone, qrcode);
        this.services = services;
        this.documents = documents;
        this.memberStats = memberStats;
        this.milkStats = milkStats;
    }

    public List<ServiceDTO> getServices() {
        return services;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public MemberStatsDTO getMemberStats() {
        return memberStats;
    }

    public MilkStatsDTO getMilkStats() {
        return milkStats;
    }
}
