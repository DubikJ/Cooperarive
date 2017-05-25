package com.avatlantik.cooperative.util;

import com.avatlantik.cooperative.model.db.Document;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.model.db.MilkParam;
import com.avatlantik.cooperative.model.db.ServiceDemand;
import com.avatlantik.cooperative.model.db.Visit;
import com.avatlantik.cooperative.model.json.DocumentDTO;
import com.avatlantik.cooperative.model.json.MilkParamDTO;
import com.avatlantik.cooperative.model.json.ServiceDTO;
import com.avatlantik.cooperative.model.json.UploadMemberDTO;
import com.avatlantik.cooperative.model.json.VisitDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Db2JsonModelConverter {
    public static UploadMemberDTO convertMember(Member member, Visit visit, MilkParam milkParams,
                                                List<ServiceDemand> demandServices, List<Document> documents) {
        return new UploadMemberDTO(
                member.getExternalId(),
                member.getName(),
                member.getAddress(),
                member.getPhone(),
                member.getQrcode(),
                convertVisit(visit, milkParams, demandServices, documents)
        );
    }

    public static UploadMemberDTO convertMember(Member member) {
        return new UploadMemberDTO(
                member.getExternalId(),
                member.getName(),
                member.getAddress(),
                member.getPhone(),
                member.getQrcode(),
                null
        );
    }

    private static VisitDTO convertVisit(Visit visit, MilkParam milkParams,
                                         List<ServiceDemand> demandServices, List<Document> documents) {
        return new VisitDTO(
                visit.getDate(),
                milkParams == null ? null :
                        MilkParamDTO.builder()
                                .fat(milkParams.getFat())
                                .snf(milkParams.getSnf())
                                .dencity(milkParams.getDencity())
                                .addedWater(milkParams.getAddedWater())
                                .fp(milkParams.getFp())
                                .protein(milkParams.getProtein())
                                .conductivity(milkParams.getConductivity())
                                .volume(milkParams.getVolume())
                                .ekomilk(milkParams.isEkomilk())
                                .build(),
                convertServices(demandServices),
                convertDocuments(documents)
        );
    }

    private static List<DocumentDTO> convertDocuments(List<Document> documents) {
        if (documents == null) return new ArrayList<>();
        List<DocumentDTO> result = new ArrayList<>(documents.size());
        for (Document document : documents) {
            result.add(new DocumentDTO(document.getCode(), document.getValue()));
        }
        return result;
    }

    private static List<ServiceDTO> convertServices(List<ServiceDemand> demandServices) {
        if (demandServices == null || demandServices.isEmpty()) return Collections.emptyList();

        List<ServiceDTO> services = new ArrayList<>();

        for (ServiceDemand demand : demandServices) {
            services.add(new ServiceDTO(demand.getCode(), demand.getValue()));
        }

        return services;
    }
}
