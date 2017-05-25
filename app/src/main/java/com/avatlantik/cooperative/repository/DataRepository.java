package com.avatlantik.cooperative.repository;

import com.avatlantik.cooperative.model.ParameterInfo;
import com.avatlantik.cooperative.model.db.Document;
import com.avatlantik.cooperative.model.db.DocumentCode;
import com.avatlantik.cooperative.model.db.DocumentStats;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.model.db.MemberStats;
import com.avatlantik.cooperative.model.db.MilkParam;
import com.avatlantik.cooperative.model.db.MilkStats;
import com.avatlantik.cooperative.model.db.ServiceCode;
import com.avatlantik.cooperative.model.db.ServiceDelivery;
import com.avatlantik.cooperative.model.db.ServiceDemand;
import com.avatlantik.cooperative.model.db.Track;
import com.avatlantik.cooperative.model.db.TrackMember;
import com.avatlantik.cooperative.model.db.Visit;

import java.util.Date;
import java.util.List;

public interface DataRepository {

    List<Member> getMembers(String trackId);

    List<Member> getChangedMembers(String trackId);

    Member getMemberById(int id);

    Boolean isMemberChanged(String MemberExternalId);

    Member getMemberByExternalId(String externalId);

    Member getMemberByQrCode(String qrCode);

    MilkParam getMilkParams(int visitId);

    Double getTotalLitresByTrack(Date date);

    List<ServiceCode> getServiceCodes();

    List<ServiceCode> getServiceCodesByParent(String parentId);

    ServiceCode getServiceCodeByExternalId(String externalId);

    List<DocumentCode> getDocumentCodes();

    List<ServiceDemand> getDemandServices(int visitId);

    List<ServiceDelivery> getDeliveryServices(String trackExternalId, String memberExternalId);

    Visit getVisit(int memberId, Date date);

    Track getLatestTrack();

    Track getTrackById(String externalId);

    TrackMember getTrackMemberByMemberExternalId(String trackExternalId, String MemberExternalId);

    List<TrackMember> getTrackMembers(String id);

    List<Member> getMembersByTrackAndPosition(String id, List<String> blackList, int numRows);

    String getUserSetting(String settingId);

    void insertTrackInfo(Track track);

    void insertTrackMember(String trackId, Member member);

    void insertTrackMembers(String trackId, List<Member> members);

    void insertMember(Member member);

    void setMemberChanged(String MemberExternalId);

    void updateMemberExternalId(String trackId, String appExternalId, String newExternalId);

    void insertMemberStats(List<MemberStats> memberStats);

    void insertMilkStats(List<MilkStats> milkStats);

    void saveServiceDemand(ServiceDemand serviceCode);

    void saveServiceCodes(List<ServiceCode> serviceCodeList);

    void saveDocumentCodes(List<DocumentCode> documentCodes);

    void saveMilkParam(MilkParam milkParam);

    void saveServices(List<ServiceDelivery> services);

    void saveVisit(Visit milkParam);

    void clearDataBase();

    void deleteVisitsByPeriod();

    void insertDocsStats(List<DocumentStats> lists);

    void insertUserSetting(ParameterInfo usersetting);

    void insertUserSettings(List<ParameterInfo> list);

    List<DocumentStats> getDocStatsByMemberId(String memberExternalId);

    List<Document> getDocuments(int visitId);

    MilkStats getMilkStats(String memberExternalId);

    MemberStats getMemberStats(String memberExternalId);
}
