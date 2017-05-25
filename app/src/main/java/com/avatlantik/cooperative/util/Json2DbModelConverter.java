package com.avatlantik.cooperative.util;

import com.avatlantik.cooperative.model.db.DocumentCode;
import com.avatlantik.cooperative.model.db.DocumentStats;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.model.db.MemberStats;
import com.avatlantik.cooperative.model.db.MilkStats;
import com.avatlantik.cooperative.model.db.ServiceCode;
import com.avatlantik.cooperative.model.db.ServiceDelivery;
import com.avatlantik.cooperative.model.db.Track;
import com.avatlantik.cooperative.model.db.TrackMember;
import com.avatlantik.cooperative.model.json.DocumentCodeDTO;
import com.avatlantik.cooperative.model.json.DocumentDTO;
import com.avatlantik.cooperative.model.json.DownloadMemberDTO;
import com.avatlantik.cooperative.model.json.MemberDTO;
import com.avatlantik.cooperative.model.json.ServiceCodeDTO;
import com.avatlantik.cooperative.model.json.ServiceDTO;
import com.avatlantik.cooperative.model.json.TrackDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Json2DbModelConverter {

    public static Track convertTrack(TrackDTO track) {
        return Track.builder().externalId(track.getId()).name(track.getName()).date(track.getDate()).build();
    }

    public static List<Member> convertMembers(List<DownloadMemberDTO> members) {
        if (members == null) return Collections.emptyList();
        List<Member> result = new ArrayList<>();
        for (MemberDTO member : members) {
            result.add(
                    Member.builder()
                            .externalId(member.getExternalId())
                            .name(member.getName())
                            .address(member.getAddress())
                            .phone(member.getPhone())
                            .qrcode(member.getQrcode())
                            .build());
        }
        return result;
    }

    public static List<MemberStats> convertMemberStats(List<DownloadMemberDTO> members) {
        if (members == null) return Collections.emptyList();
        List<MemberStats> result = new ArrayList<>();
        for (DownloadMemberDTO member : members) {
            if (member.getMemberStats() == null)
                continue;
            result.add(
                    MemberStats.builder()
                            .memberId(member.getExternalId())
                            .companyLoan(member.getMemberStats().getCompanyLoan())
                            .memberLoan(member.getMemberStats().getMemberLoan())
                            .milkVolume(member.getMemberStats().getMilkVolume())
                            .build());
        }
        return result;

    }

    public static List<MilkStats> convertMilkStats(List<DownloadMemberDTO> members) {
        if (members == null) return Collections.emptyList();
        List<MilkStats> result = new ArrayList<>();
        for (DownloadMemberDTO member : members) {
            if (member.getMilkStats() == null)
                continue;
            result.add(
                    MilkStats.builder()
                            .memberId(member.getExternalId())
                            .fat(member.getMilkStats().getFat())
                            .snf(member.getMilkStats().getSnf())
                            .dencity(member.getMilkStats().getDencity())
                            .addedWater(member.getMilkStats().getAddedWater())
                            .fp(member.getMilkStats().getFp())
                            .protein(member.getMilkStats().getProtein())
                            .conductivity(member.getMilkStats().getConductivity())
                            .volume(member.getMilkStats().getVolume())
                            .build());
        }
        return result;

    }

    public static List<DocumentStats> convertDocsStats(List<DownloadMemberDTO> members) {
        if (members == null) return Collections.emptyList();
        List<DocumentStats> result = new ArrayList<>();
        for (DownloadMemberDTO member : members) {
            List<DocumentStats> documentStatsList = new ArrayList<>();
            for (DocumentDTO documentStats : member.getDocuments()) {
                if (documentStats == null)
                    continue;
                documentStatsList.add(
                        DocumentStats.builder()
                                .memberId(member.getExternalId())
                                .code(documentStats.getCode())
                                .value(documentStats.getValue())
                                .build());
            }
            result.addAll(documentStatsList);
        }
        return result;

    }


    public static List<ServiceDelivery> convertServicesDelivery(List<DownloadMemberDTO> members, List<TrackMember> trackMembers) {
        Map<String, Integer> trackMembersIdByMemberId = new HashMap<>(trackMembers.size());
        for (TrackMember trackMember : trackMembers) {
            trackMembersIdByMemberId.put(trackMember.getMemberExternalId(), trackMember.getId());
        }
        if (members == null) return Collections.emptyList();
        List<ServiceDelivery> result = new ArrayList<>();
        for (DownloadMemberDTO member : members) {
            if (member.getServices() != null) {
                for (ServiceDTO service : member.getServices()) {
                    result.add(ServiceDelivery.builder()
                            .trackMemberId(trackMembersIdByMemberId.get(member.getExternalId()))
                            .code(service.getCode())
                            .value(service.getValue())
                            .build());
                }
            }
        }
        return result;
    }

    public static List<ServiceCode> convertServiceCodes(List<ServiceCodeDTO> serviceCodes) {
        if (serviceCodes == null) return Collections.emptyList();
        List<ServiceCode> result = new ArrayList<>();
        for (ServiceCodeDTO code : serviceCodes) {
            result.add(ServiceCode.builder().name(code.getName())
                                            .externalId(code.getId())
                                            .parentId(code.parentId()).build());
        }
        return result;
    }

    public static List<DocumentCode> convertDocumentCodes(List<DocumentCodeDTO> documentCodes) {
        if (documentCodes == null) return Collections.emptyList();
        List<DocumentCode> result = new ArrayList<>();
        for (DocumentCodeDTO code : documentCodes) {
            result.add(DocumentCode.builder().name(code.getName()).externalId(code.getId()).build());
        }
        return result;
    }
}
