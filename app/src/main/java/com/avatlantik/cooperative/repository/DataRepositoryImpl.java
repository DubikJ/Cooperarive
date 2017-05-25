package com.avatlantik.cooperative.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.avatlantik.cooperative.db.CooperativeContract.DocumentCodeContract;
import com.avatlantik.cooperative.db.CooperativeContract.DocumentContract;
import com.avatlantik.cooperative.db.CooperativeContract.DocumentStatsContract;
import com.avatlantik.cooperative.db.CooperativeContract.MemberStatsContract;
import com.avatlantik.cooperative.db.CooperativeContract.MilkStatsContract;
import com.avatlantik.cooperative.db.CooperativeContract.ServiceCodeContract;
import com.avatlantik.cooperative.db.CooperativeContract.TrackInfoContract;
import com.avatlantik.cooperative.db.CooperativeContract.UserSettings;
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

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.avatlantik.cooperative.common.Consts.MINIMUM_NUMBERS_DAYS_VISITS;
import static com.avatlantik.cooperative.db.CooperativeContract.MemberContract;
import static com.avatlantik.cooperative.db.CooperativeContract.MilkParamsContract;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceDeliveryContract;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceDemandContract;
import static com.avatlantik.cooperative.db.CooperativeContract.TrackMemberContract;
import static com.avatlantik.cooperative.db.CooperativeContract.TrackMemberContract.TRACK_EXTERNAL_ID;
import static com.avatlantik.cooperative.db.CooperativeContract.UserSettings.USER_SETTING_ID;
import static com.avatlantik.cooperative.db.CooperativeContract.VisitContract;
import static com.avatlantik.cooperative.repository.ModelConverter.buildDocument;
import static com.avatlantik.cooperative.repository.ModelConverter.buildMember;
import static com.avatlantik.cooperative.repository.ModelConverter.buildServiceDelivery;
import static com.avatlantik.cooperative.repository.ModelConverter.buildServiceDemand;
import static com.avatlantik.cooperative.repository.ModelConverter.buildTrack;
import static com.avatlantik.cooperative.repository.ModelConverter.buildTrackMember;
import static com.avatlantik.cooperative.repository.ModelConverter.buildVisit;
import static com.avatlantik.cooperative.repository.ModelConverter.convertCode;
import static com.avatlantik.cooperative.repository.ModelConverter.convertDocumentStats;
import static com.avatlantik.cooperative.repository.ModelConverter.convertMember;
import static com.avatlantik.cooperative.repository.ModelConverter.convertMemberStats;
import static com.avatlantik.cooperative.repository.ModelConverter.convertMilkParam;
import static com.avatlantik.cooperative.repository.ModelConverter.convertMilkStats;
import static com.avatlantik.cooperative.repository.ModelConverter.convertService;
import static com.avatlantik.cooperative.repository.ModelConverter.convertServiceCodes;
import static com.avatlantik.cooperative.repository.ModelConverter.convertTrackInfo;
import static com.avatlantik.cooperative.repository.ModelConverter.convertTrackMember;

public class DataRepositoryImpl implements DataRepository {

    private ContentResolver contentResolver;

    public DataRepositoryImpl(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public List<Member> getMembers(String trackExternalId) {
        if (TextUtils.isEmpty(trackExternalId)) return Collections.emptyList();
        return getMembersByTrack(trackExternalId);
    }

    @Override
    public List<Member> getChangedMembers(String trackExternalId) {
        if (TextUtils.isEmpty(trackExternalId)) return Collections.emptyList();
        return getChangedMembersByTrack(trackExternalId);
    }

    @Override
    public Member getMemberById(int id) {
        try (Cursor cursor = contentResolver.query(
                ContentUris.withAppendedId(MemberContract.CONTENT_URI, id),
                MemberContract.PROJECTION_ALL, MemberContract._ID + "=" + id,
                null, MemberContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                return buildMember(cursor);
            } else {
                return null;
            }
        }
    }

    @Override
    public Boolean isMemberChanged(String externalId) {
        Track latestTrack = getLatestTrack();
        if(latestTrack == null) return false;

        try (Cursor cursor = contentResolver.query(
                TrackMemberContract.CONTENT_URI,
                TrackMemberContract.PROJECTION_ALL, TrackMemberContract.MEMBER_EXTERNAL_ID + "='" + externalId + "' AND "+
                        TrackMemberContract.TRACK_EXTERNAL_ID + "='" + latestTrack.getExternalId() + "'",
                null, TrackMemberContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;

            return cursor.getInt(cursor.getColumnIndex(TrackMemberContract.MEMBER_CHANGED)) == 1;
        }
    }

    @Override
    public Member getMemberByExternalId(String externalId) {
        try (Cursor cursor = contentResolver.query(
                MemberContract.CONTENT_URI,
                MemberContract.PROJECTION_ALL, MemberContract.EXTERNAL_ID + "='" + externalId + "'",
                null, MemberContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                return buildMember(cursor);
            } else {
                return null;
            }
        }
    }


    @Override
    public Member getMemberByQrCode(String qrCode) {
        try (Cursor cursor = contentResolver.query(MemberContract.CONTENT_URI,
                MemberContract.PROJECTION_ALL, MemberContract.QR_CODE + " =?",
                new String[]{qrCode}, MemberContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;
            return buildMember(cursor);
        }
    }

    @Override
    public Track getLatestTrack() {
        try (Cursor cursor = contentResolver.query(TrackInfoContract.CONTENT_URI,
                new String[]{"count()"}, null,
                null, TrackInfoContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(cursor.getColumnIndex("count()")) == 0)
                return null;
        }

        try (Cursor cursor = contentResolver.query(TrackInfoContract.CONTENT_URI,
                TrackInfoContract.PROJECTION_ALL, TrackInfoContract.DATE
                        + " = (select MAX(" + TrackInfoContract.DATE + ") from " + TrackInfoContract.TABLE_NAME + ")",
                null, TrackInfoContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;
            return buildTrack(cursor);
        }
    }

    @Override
    public Track getTrackById(String externalId) {
        try (Cursor cursor = contentResolver.query(TrackInfoContract.CONTENT_URI,
                TrackInfoContract.PROJECTION_ALL, TrackInfoContract.EXTERNAL_ID + " = ?",
                new String[]{externalId}, TrackInfoContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;
            return buildTrack(cursor);
        }
    }

    @Override
    public TrackMember getTrackMemberByMemberExternalId(String trackExternalId, String MemberExternalId) {
        try (Cursor cursor = contentResolver.query(TrackMemberContract.CONTENT_URI,
                TrackMemberContract.PROJECTION_ALL,
                TrackMemberContract.TRACK_EXTERNAL_ID + "= ? AND " + TrackMemberContract.MEMBER_EXTERNAL_ID + "= ?",
                new String[]{trackExternalId, MemberExternalId}, TrackMemberContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return null;
            return buildTrackMember(cursor);
        }
    }

    @Override
    public List<TrackMember> getTrackMembers(String id) {
        try (Cursor cursor = contentResolver.query(TrackMemberContract.CONTENT_URI,
                TrackMemberContract.PROJECTION_ALL, TRACK_EXTERNAL_ID + " = ?",
                new String[]{id}, TrackMemberContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<TrackMember> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(buildTrackMember(cursor));
            return result;
        }
    }

    @Override
    public String getUserSetting(String settingId) {
        try (Cursor cursor = contentResolver.query(UserSettings.CONTENT_URI,
                UserSettings.PROJECTION_ALL, USER_SETTING_ID + " = ?",
                new String[]{settingId}, UserSettings.DEFAULT_SORT_ORDER)) {

            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(UserSettings.SETTING_VALUE));
            } else {
                return null;
            }
        }
    }

    @Override
    public List<Document> getDocuments(int visitId) {
        try (Cursor cursor = contentResolver.query(DocumentContract.CONTENT_URI,
                DocumentContract.PROJECTION_ALL, DocumentContract.VISIT_ID + " = " + visitId,
                new String[]{}, DocumentContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<Document> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(buildDocument(cursor));
            return result;

        }
    }

    @Override
    public MilkStats getMilkStats(String memberExternalId) {
        try (Cursor cursor = contentResolver.query(
                MilkStatsContract.CONTENT_URI,
                MilkStatsContract.PROJECTION_ALL,
                MilkStatsContract.MEMBER_ID + "=?",
                new String[]{memberExternalId},
                MilkStatsContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }
            return MilkStats.builder()
                    .id(cursor.getInt(cursor.getColumnIndex(MilkStatsContract._ID)))
                    .memberId(memberExternalId)
                    .fat(cursor.getDouble(cursor.getColumnIndex(MilkStatsContract.FAT)))
                    .snf(cursor.getDouble(cursor.getColumnIndex(MilkStatsContract.SNF)))
                    .dencity(cursor.getDouble(cursor.getColumnIndex(MilkStatsContract.DENCITY)))
                    .addedWater(cursor.getDouble(cursor.getColumnIndex(MilkStatsContract.ADDED_WATER)))
                    .fp(cursor.getDouble(cursor.getColumnIndex(MilkStatsContract.FP)))
                    .protein(cursor.getDouble(cursor.getColumnIndex(MilkStatsContract.PROTEIN)))
                    .conductivity(cursor.getDouble(cursor.getColumnIndex(MilkStatsContract.CONDUCTIVITY)))
                    .volume(cursor.getDouble(cursor.getColumnIndex(MilkStatsContract.VOLUME)))
                    .build();
        }
    }

    @Override
    public MemberStats getMemberStats(String memberExternalId) {
        try (Cursor cursor = contentResolver.query(
                MemberStatsContract.CONTENT_URI,
                MemberStatsContract.PROJECTION_ALL,
                MemberStatsContract.MEMBER_ID + "=?",
                new String[]{memberExternalId},
                MemberStatsContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }
            return MemberStats.builder()
                    .id(cursor.getInt(cursor.getColumnIndex(MemberStatsContract._ID)))
                    .memberId(memberExternalId)
                    .memberLoan(cursor.getDouble(cursor.getColumnIndex(MemberStatsContract.MEMBER_LOAN)))
                    .companyLoan(cursor.getDouble(cursor.getColumnIndex(MemberStatsContract.COMPANY_LOAN)))
                    .milkVolume(cursor.getDouble(cursor.getColumnIndex(MemberStatsContract.MILK_VOLUME)))
                    .build();
        }
    }

    @Override
    public MilkParam getMilkParams(int visitId) {
        try (Cursor cursor = contentResolver.query(
                MilkParamsContract.CONTENT_URI,
                MilkParamsContract.PROJECTION_ALL,
                MilkParamsContract.VISIT_ID + "=" + visitId,
                new String[]{},
                MilkParamsContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }
            return MilkParam.builder()
                    .id(cursor.getInt(cursor.getColumnIndex(MilkParamsContract._ID)))
                    .visitId(cursor.getInt(cursor.getColumnIndex(MilkParamsContract.VISIT_ID)))
                    .fat(cursor.getDouble(cursor.getColumnIndex(MilkParamsContract.FAT)))
                    .snf(cursor.getDouble(cursor.getColumnIndex(MilkParamsContract.SNF)))
                    .dencity(cursor.getDouble(cursor.getColumnIndex(MilkParamsContract.DENCITY)))
                    .addedWater(cursor.getDouble(cursor.getColumnIndex(MilkParamsContract.ADDED_WATER)))
                    .fp(cursor.getDouble(cursor.getColumnIndex(MilkParamsContract.FP)))
                    .protein(cursor.getDouble(cursor.getColumnIndex(MilkParamsContract.PROTEIN)))
                    .conductivity(cursor.getDouble(cursor.getColumnIndex(MilkParamsContract.CONDUCTIVITY)))
                    .volume(cursor.getDouble(cursor.getColumnIndex(MilkParamsContract.VOLUME)))
                    .isEkomilk(cursor.getInt(cursor.getColumnIndex(MilkParamsContract.EKOMILK)) == 1)
                    .build();
        }
    }

    @Override
    public Double getTotalLitresByTrack(Date date) {
        List<String> visits = new ArrayList<>();
        try (Cursor cursor = contentResolver.query(
                VisitContract.CONTENT_URI,
                new String[]{VisitContract._ID},
                VisitContract.DATE + ">= "+ date.getTime(),
                null,
                VisitContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) return 0.0;

            while (cursor.moveToNext()) {
                visits.add(cursor.getString(cursor.getColumnIndex(VisitContract._ID)));
            }
        }

        try (Cursor cursorMilk = contentResolver.query(
                MilkParamsContract.CONTENT_URI,
                new String[] { "sum(" + MilkParamsContract.VOLUME + ")" },
                MilkParamsContract.VISIT_ID + " IN ('" + TextUtils.join("','", visits) + "')",
                new String[]{},
                MilkParamsContract.DEFAULT_SORT_ORDER)) {

            if (cursorMilk == null || !cursorMilk.moveToFirst()) return 0.0;

            return cursorMilk.getDouble(0);
        }
    }

    @Override
    public List<ServiceCode> getServiceCodes() {
        try (Cursor cursor = contentResolver.query(
                ServiceCodeContract.CONTENT_URI,
                ServiceCodeContract.PROJECTION_ALL,
                null,
                null,
                ServiceCodeContract.DEFAULT_SORT_ORDER)) {
            if (cursor == null)
                return null;

            List<ServiceCode> serviceCodes = new ArrayList<>();
            while (cursor.moveToNext())
                serviceCodes.add(
                        ServiceCode.builder()
                                .id(cursor.getInt(cursor.getColumnIndex(ServiceCodeContract._ID)))
                                .externalId(cursor.getString(cursor.getColumnIndex(ServiceCodeContract.EXTERNAL_ID)))
                                .name(cursor.getString(cursor.getColumnIndex(ServiceCodeContract.NAME)))
                                .parentId(cursor.getString(cursor.getColumnIndex(ServiceCodeContract.PARENT_ID)))
                                .build());
            return serviceCodes;
        }
    }

    @Override
    public List<ServiceCode> getServiceCodesByParent(String parentId) {
        try (Cursor cursor = contentResolver.query(
                ServiceCodeContract.CONTENT_URI,
                ServiceCodeContract.PROJECTION_ALL,
                ServiceCodeContract.PARENT_ID + "=?",
                new String[]{parentId},
                ServiceCodeContract.DEFAULT_SORT_ORDER)) {
            if (cursor == null)
                return null;

            List<ServiceCode> serviceCodes = new ArrayList<>();
            while (cursor.moveToNext())
                serviceCodes.add(
                        ServiceCode.builder()
                                .id(cursor.getInt(cursor.getColumnIndex(ServiceCodeContract._ID)))
                                .externalId(cursor.getString(cursor.getColumnIndex(ServiceCodeContract.EXTERNAL_ID)))
                                .name(cursor.getString(cursor.getColumnIndex(ServiceCodeContract.NAME)))
                                .parentId(cursor.getString(cursor.getColumnIndex(ServiceCodeContract.PARENT_ID)))
                                .build());
            return serviceCodes;
        }
    }

    @Override
    public ServiceCode getServiceCodeByExternalId(String ExternalId) {
        try (Cursor cursor = contentResolver.query(
                ServiceCodeContract.CONTENT_URI,
                ServiceCodeContract.PROJECTION_ALL,
                ServiceCodeContract.EXTERNAL_ID + "=?",
                new String[]{ExternalId},
                ServiceCodeContract.DEFAULT_SORT_ORDER)) {
            if (cursor == null)
                return null;

            if (cursor == null || !cursor.moveToFirst()) return null;
            return ServiceCode.builder()
                    .id(cursor.getInt(cursor.getColumnIndex(ServiceCodeContract._ID)))
                    .externalId(cursor.getString(cursor.getColumnIndex(ServiceCodeContract.EXTERNAL_ID)))
                    .name(cursor.getString(cursor.getColumnIndex(ServiceCodeContract.NAME)))
                    .parentId(cursor.getString(cursor.getColumnIndex(ServiceCodeContract.PARENT_ID)))
                    .build();
        }
    }

    @Override
    public List<DocumentCode> getDocumentCodes() {
        try (Cursor cursor = contentResolver.query(
                DocumentCodeContract.CONTENT_URI,
                DocumentCodeContract.PROJECTION_ALL,
                null,
                null,
                DocumentCodeContract.DEFAULT_SORT_ORDER)) {
            if (cursor == null)
                return null;

            List<DocumentCode> documentCodes = new ArrayList<>();
            while (cursor.moveToNext())
                documentCodes.add(
                        DocumentCode.builder()
                                .id(cursor.getInt(cursor.getColumnIndex(DocumentCodeContract._ID)))
                                .externalId(cursor.getString(cursor.getColumnIndex(DocumentCodeContract.EXTERNAL_ID)))
                                .name(cursor.getString(cursor.getColumnIndex(DocumentCodeContract.NAME)))
                                .build());
            return documentCodes;
        }
    }

    @Override
    public List<ServiceDemand> getDemandServices(int visitId) {
        try (Cursor cursor = contentResolver.query(ServiceDemandContract.CONTENT_URI,
                ServiceDemandContract.PROJECTION_ALL, ServiceDemandContract.VISIT_ID + " = " + visitId,
                new String[]{}, ServiceDemandContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return null;
            List<ServiceDemand> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(buildServiceDemand(cursor));
            return result;

        }
    }

    @Override
    public List<ServiceDelivery> getDeliveryServices(String trackExternalId, String memberExternalId) {
        TrackMember trackMember = getTrackMemberByMemberExternalId(trackExternalId, memberExternalId);
        if (trackMember == null) return null;
        try (Cursor cursor = contentResolver.query(ServiceDeliveryContract.CONTENT_URI,
                     ServiceDeliveryContract.PROJECTION_ALL, ServiceDeliveryContract.TRACK_MEMBER_ID + " = " + trackMember.getId(),
                     new String[]{}, ServiceDeliveryContract.DEFAULT_SORT_ORDER)) {
            if (cursor == null) return null;
            List<ServiceDelivery> result = new ArrayList<>();
            while (cursor.moveToNext())
                result.add(buildServiceDelivery(cursor));
            return result;

        }
    }

    @Override
    public Visit getVisit(int memberId, Date date) {
        try (Cursor cursor = contentResolver.query(
                VisitContract.CONTENT_URI,
                VisitContract.PROJECTION_ALL,
                VisitContract.MEMBER_ID + "=" + memberId + " AND "
                        + VisitContract.DATE + ">=" + date.getTime(),
                new String[]{},
                VisitContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }
            return buildVisit(cursor);
        }
    }


    @Override
    public void insertMember(Member member) {
        ContentValues values = convertMember(member);
        contentResolver.insert(MemberContract.CONTENT_URI, values);
    }

    @Override
    public void setMemberChanged(String externalId) {
        Track latestTrack = getLatestTrack();
        if(latestTrack == null) return;
        ContentValues values = new ContentValues();
        values.put(TrackMemberContract.MEMBER_CHANGED, true);
        contentResolver.update(
                TrackMemberContract.CONTENT_URI,
                values,
                TrackMemberContract.MEMBER_EXTERNAL_ID + "='" + externalId + "' AND "
                        + TRACK_EXTERNAL_ID + "='" + latestTrack.getExternalId() + "'",
                null);

    }

    @Override
    public void updateMemberExternalId(String trackId, String appExternalId, String newExternalId) {
        ContentValues newMemberContentValues = new ContentValues();
        newMemberContentValues.put(MemberContract.EXTERNAL_ID, newExternalId);


        contentResolver.update(
                MemberContract.CONTENT_URI,
                newMemberContentValues, MemberContract.EXTERNAL_ID + "='" + appExternalId + "'", null);

        ContentValues newTrackMemberContentValues = new ContentValues();
        newTrackMemberContentValues.put(TrackMemberContract.MEMBER_EXTERNAL_ID, newExternalId);

        contentResolver.update(
                TrackMemberContract.CONTENT_URI,
                newTrackMemberContentValues,
                TrackMemberContract.MEMBER_EXTERNAL_ID + "='" + appExternalId + "' AND "
                        + TRACK_EXTERNAL_ID + "='" + trackId + "'",
                null);
    }

    @Override
    public void insertMemberStats(List<MemberStats> memberStatsList) {
        List<ContentValues> memberStatsContent = new ArrayList<>();
        for (MemberStats memberStats : memberStatsList) {
            memberStatsContent.add(convertMemberStats(memberStats));
        }
        contentResolver.bulkInsert(
                MemberStatsContract.CONTENT_URI,
                memberStatsContent.toArray(new ContentValues[memberStatsContent.size()]));
    }

    @Override
    public void insertMilkStats(List<MilkStats> milkStatsList) {
        List<ContentValues> milkStatsContent = new ArrayList<>();
        for (MilkStats milkStats : milkStatsList) {
            milkStatsContent.add(convertMilkStats(milkStats));
        }
        contentResolver.bulkInsert(
                MilkStatsContract.CONTENT_URI,
                milkStatsContent.toArray(new ContentValues[milkStatsContent.size()]));
    }

    @Override
    public void insertDocsStats(List<DocumentStats> documents) {
        List<ContentValues> documentStatsContent = new ArrayList<>();
        for (DocumentStats documentStats : documents) {
            documentStatsContent.add(convertDocumentStats(documentStats));
        }
        contentResolver.bulkInsert(
                DocumentStatsContract.CONTENT_URI,
                documentStatsContent.toArray(new ContentValues[documentStatsContent.size()]));
    }

    @Override
    public void insertUserSetting(ParameterInfo usersetting) {

        ContentValues values = new ContentValues();
        values.put(USER_SETTING_ID, usersetting.getName());
        values.put(UserSettings.SETTING_VALUE, usersetting.getValue());

        contentResolver.insert(UserSettings.CONTENT_URI, values);
    }

    @Override
    public void insertUserSettings(List<ParameterInfo> userSettingList) {
        if (userSettingList == null || userSettingList.isEmpty()) return;

        List<ContentValues> values = new ArrayList<>();
        for (ParameterInfo setting : userSettingList) {
            values.add(convertCode(setting.getName(), setting.getValue()));
        }
        contentResolver.bulkInsert(UserSettings.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

    @Override
    public List<DocumentStats> getDocStatsByMemberId(String memberExternalId) {
        try (Cursor cursor = contentResolver.query(
                DocumentStatsContract.CONTENT_URI,
                DocumentStatsContract.PROJECTION_ALL,
                DocumentStatsContract.MEMBER_ID + "='" + memberExternalId + "'",
                new String[]{},
                DocumentStatsContract.DEFAULT_SORT_ORDER)) {
            if (cursor == null) return null;

            List<DocumentStats> documentStats = new ArrayList<>();
            while (cursor.moveToNext())
                documentStats.add(
                        DocumentStats.builder()
                                .id(cursor.getInt(cursor.getColumnIndex(DocumentStatsContract._ID)))
                                .memberId(cursor.getString(cursor.getColumnIndex(DocumentStatsContract.MEMBER_ID)))
                                .code(cursor.getString(cursor.getColumnIndex(DocumentStatsContract.CODE)))
                                .value(cursor.getInt(cursor.getColumnIndex(DocumentStatsContract.VALUE)) == 1)
                                .build());
            return documentStats;
        }
    }

    @Override
    public void insertTrackMember(String trackId, Member member) {
        contentResolver.insert(
                TrackMemberContract.CONTENT_URI,
                convertTrackMember(
                        TrackMember.builder()
                                .memberExternalId(member.getExternalId())
                                .trackExternalId(trackId)
                                .build()));
    }

    @Override
    public void insertTrackMembers(String trackId, List<Member> members) {
        List<ContentValues> membersContent = new ArrayList<>();
        List<ContentValues> trackMembersContent = new ArrayList<>();
        for (Member member : members) {
            membersContent.add(convertMember(member));
            trackMembersContent.add(convertTrackMember(
                    TrackMember.builder()
                            .memberExternalId(member.getExternalId())
                            .trackExternalId(trackId)
                            .build()));
        }
        contentResolver.bulkInsert(
                MemberContract.CONTENT_URI,
                membersContent.toArray(new ContentValues[membersContent.size()]));
        contentResolver.bulkInsert(
                TrackMemberContract.CONTENT_URI,
                trackMembersContent.toArray(new ContentValues[trackMembersContent.size()]));
    }


    @Override
    public void saveServiceDemand(ServiceDemand service) {
        ContentValues values = new ContentValues();
        values.put(ServiceDemandContract.VISIT_ID, service.getVisitId());
        values.put(ServiceDemandContract.CODE, service.getCode());
        values.put(ServiceDemandContract.VALUE, service.getValue());
        contentResolver.insert(ServiceDemandContract.CONTENT_URI, values);

    }


    @Override
    public void saveServiceCodes(List<ServiceCode> serviceCodeList) {
        if (serviceCodeList == null || serviceCodeList.isEmpty()) return;

        List<ContentValues> values = new ArrayList<>();
        for (ServiceCode code : serviceCodeList) {
            values.add(convertServiceCodes(code.getExternalId(), code.getName(), code.getParentId()));
        }
        contentResolver.bulkInsert(ServiceCodeContract.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

    @Override
    public void saveDocumentCodes(List<DocumentCode> documentCodes) {
        if (documentCodes == null || documentCodes.isEmpty()) return;

        List<ContentValues> values = new ArrayList<>();
        for (DocumentCode code : documentCodes) {
            values.add(convertCode(code.getExternalId(), code.getName()));
        }
        contentResolver.bulkInsert(DocumentCodeContract.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

    @Override
    public void saveMilkParam(MilkParam milkParam) {
        ContentValues values = convertMilkParam(milkParam);
        contentResolver.insert(MilkParamsContract.CONTENT_URI, values);
    }

    @Override
    public void saveServices(List<ServiceDelivery> services) {
        if (services == null || services.isEmpty()) return;

        List<ContentValues> values = new ArrayList<>();
        for (ServiceDelivery service : services) {
            values.add(convertService(service));
        }
        contentResolver.bulkInsert(ServiceDeliveryContract.CONTENT_URI, values.toArray(new ContentValues[values.size()]));
    }

    @Override
    public void saveVisit(Visit visit) {
        ContentValues values = new ContentValues();
        values.put(VisitContract.MEMBER_ID, visit.getMemberId());
        values.put(VisitContract.DATE, visit.getDate().getTime() + TimeZone.getDefault().getRawOffset());
        contentResolver.insert(VisitContract.CONTENT_URI, values);
    }

    @Override
    public void clearDataBase() {
            contentResolver.delete(MemberContract.CONTENT_URI, null, null);
            contentResolver.delete(TrackInfoContract.CONTENT_URI, null, null);
            contentResolver.delete(TrackMemberContract.CONTENT_URI, null, null);
            contentResolver.delete(VisitContract.CONTENT_URI, null, null);
            contentResolver.delete(ServiceDeliveryContract.CONTENT_URI, null, null);
            contentResolver.delete(ServiceDemandContract.CONTENT_URI, null, null);
            contentResolver.delete(ServiceCodeContract.CONTENT_URI, null, null);
            contentResolver.delete(DocumentContract.CONTENT_URI, null, null);
            contentResolver.delete(DocumentCodeContract.CONTENT_URI, null, null);
            contentResolver.delete(MilkParamsContract.CONTENT_URI, null, null);
            contentResolver.delete(MilkStatsContract.CONTENT_URI, null, null);
            contentResolver.delete(MemberStatsContract.CONTENT_URI, null, null);
            contentResolver.delete(DocumentStatsContract.CONTENT_URI, null, null);
            contentResolver.delete(UserSettings.CONTENT_URI, null, null);
    }

    @Override
    public void deleteVisitsByPeriod() {
        contentResolver.delete(VisitContract.CONTENT_URI,
                VisitContract.DATE + "<" +
                        String.valueOf(LocalDate.now().toDate().getTime() + TimeZone.getDefault().getRawOffset() -
                                (1000 * 60 * 60 * 24 *MINIMUM_NUMBERS_DAYS_VISITS))
                , null);

    }

    @Override
    public void insertTrackInfo(Track track) {
        ContentValues values = convertTrackInfo(track);
        contentResolver.insert(TrackInfoContract.CONTENT_URI, values);
    }

    private List<Member> getMembersByTrack(String externalTrackId) {
        try (Cursor cursor = contentResolver.query(TrackMemberContract.CONTENT_URI,
                new String[]{TrackMemberContract.MEMBER_EXTERNAL_ID}, TRACK_EXTERNAL_ID + "='" + externalTrackId + "'",
                null, TrackMemberContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return new ArrayList<>();

            List<String> ids = new ArrayList<>();
            while (cursor.moveToNext()) {
                ids.add(cursor.getString(cursor.getColumnIndex(TrackMemberContract.MEMBER_EXTERNAL_ID)));
            }

            return getMembers(ids);
        }
    }

    private List<Member> getChangedMembersByTrack(String externalTrackId) {
        try (Cursor cursor = contentResolver.query(TrackMemberContract.CONTENT_URI,
                new String[]{TrackMemberContract.MEMBER_EXTERNAL_ID}, TRACK_EXTERNAL_ID + "='" + externalTrackId + "' AND "+
                        TrackMemberContract.MEMBER_CHANGED + "= 1",
                null, TrackMemberContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return new ArrayList<>();

            List<String> ids = new ArrayList<>();
            while (cursor.moveToNext()) {
                ids.add(cursor.getString(cursor.getColumnIndex(TrackMemberContract.MEMBER_EXTERNAL_ID)));
            }

            return getMembers(ids);
        }
    }

    public List<Member> getMembersByTrackAndPosition(String externalTrackId, List<String> blackList, int numRows) {
        try (Cursor cursor = contentResolver.query(TrackMemberContract.CONTENT_URI,
                new String[]{TrackMemberContract.MEMBER_EXTERNAL_ID}, TRACK_EXTERNAL_ID + "='" + externalTrackId + "'" +
                        " AND "+TrackMemberContract.MEMBER_EXTERNAL_ID + " NOT IN ('" + TextUtils.join("','", blackList) + "')",
                null, TrackMemberContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null) return new ArrayList<>();

            List<String> ids = new ArrayList<>();

            while (cursor.moveToNext()) {
                ids.add(cursor.getString(cursor.getColumnIndex(TrackMemberContract.MEMBER_EXTERNAL_ID)));
            }

            if (ids.isEmpty()) return new ArrayList<>();

            try (Cursor memberCursor = contentResolver.query(MemberContract.CONTENT_URI,
                    MemberContract.PROJECTION_ALL,
                    MemberContract.EXTERNAL_ID + " IN ('" + TextUtils.join("','", ids) + "')",
                    null, String.format("%s limit "+String.valueOf(numRows), MemberContract.DEFAULT_SORT_ORDER))) {

                if (memberCursor == null) return new ArrayList<>();

                List<Member> members = new ArrayList<>();
                while (memberCursor.moveToNext()) {
                    members.add(ModelConverter.buildMember(memberCursor));
                }

                return members;
            }
        }
    }

    private List<Member> getMembers(List<String> externalIds) {
        if (externalIds.isEmpty()) return new ArrayList<>();

        try (Cursor memberCursor = contentResolver.query(MemberContract.CONTENT_URI,
                MemberContract.PROJECTION_ALL,
                MemberContract.EXTERNAL_ID + " IN ('" + TextUtils.join("','", externalIds) + "')",
                null, MemberContract.DEFAULT_SORT_ORDER)) {

            if (memberCursor == null) return new ArrayList<>();

            List<Member> members = new ArrayList<>();
            while (memberCursor.moveToNext()) {
                members.add(ModelConverter.buildMember(memberCursor));
            }

            return members;
        }
    }
}
