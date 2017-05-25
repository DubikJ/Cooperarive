package com.avatlantik.cooperative.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.avatlantik.cooperative.db.CooperativeContract;
import com.avatlantik.cooperative.db.CooperativeContract.DocumentContract;
import com.avatlantik.cooperative.db.CooperativeContract.DocumentStatsContract;
import com.avatlantik.cooperative.db.CooperativeContract.MemberStatsContract;
import com.avatlantik.cooperative.db.CooperativeContract.MilkStatsContract;
import com.avatlantik.cooperative.db.CooperativeContract.ServiceDemandContract;
import com.avatlantik.cooperative.model.db.Document;
import com.avatlantik.cooperative.model.db.DocumentStats;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.model.db.MemberStats;
import com.avatlantik.cooperative.model.db.MilkParam;
import com.avatlantik.cooperative.model.db.MilkStats;
import com.avatlantik.cooperative.model.db.ServiceDelivery;
import com.avatlantik.cooperative.model.db.ServiceDemand;
import com.avatlantik.cooperative.model.db.Track;
import com.avatlantik.cooperative.model.db.TrackMember;
import com.avatlantik.cooperative.model.db.Visit;

import java.util.Date;

import static com.avatlantik.cooperative.db.CooperativeContract.MemberContract;
import static com.avatlantik.cooperative.db.CooperativeContract.MilkParamsContract;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceCodeContract.EXTERNAL_ID;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceCodeContract.NAME;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceDeliveryContract;
import static com.avatlantik.cooperative.db.CooperativeContract.TrackInfoContract;
import static com.avatlantik.cooperative.db.CooperativeContract.TrackMemberContract;
import static com.avatlantik.cooperative.db.CooperativeContract.VisitContract;

class ModelConverter {

    static ContentValues convertMember(Member member) {
        ContentValues values = new ContentValues();
        values.put(MemberContract.EXTERNAL_ID, member.getExternalId());
        values.put(MemberContract.QR_CODE, member.getQrcode());
        values.put(MemberContract.NAME, member.getName());
        values.put(MemberContract.ADDRESS, member.getAddress());
        values.put(MemberContract.PHONE, member.getPhone());
        return values;
    }

    static ContentValues convertMemberStats(MemberStats memberStats) {
        ContentValues values = new ContentValues();
        values.put(MemberStatsContract.MEMBER_ID, memberStats.getMemberId());
        values.put(MemberStatsContract.MEMBER_LOAN, memberStats.getMemberLoan());
        values.put(MemberStatsContract.COMPANY_LOAN, memberStats.getCompanyLoan());
        values.put(MemberStatsContract.MILK_VOLUME, memberStats.getMilkVolume());
        return values;
    }

    static ContentValues convertMilkStats(MilkStats milkStats) {
        ContentValues values = new ContentValues();
        values.put(MilkStatsContract.MEMBER_ID, milkStats.getMemberId());
        values.put(MilkParamsContract.FAT, milkStats.getFat());
        values.put(MilkParamsContract.SNF, milkStats.getSnf());
        values.put(MilkParamsContract.DENCITY, milkStats.getDencity());
        values.put(MilkParamsContract.ADDED_WATER, milkStats.getAddedWater());
        values.put(MilkParamsContract.FP, milkStats.getFp());
        values.put(MilkParamsContract.PROTEIN, milkStats.getProtein());
        values.put(MilkParamsContract.CONDUCTIVITY, milkStats.getConductivity());
        values.put(MilkParamsContract.VOLUME, milkStats.getVolume());
        return values;
    }

    static ContentValues convertDocumentStats(DocumentStats documentStats) {
        ContentValues values = new ContentValues();
        values.put(DocumentStatsContract.MEMBER_ID, documentStats.getMemberId());
        values.put(DocumentStatsContract.CODE, documentStats.getCode());
        values.put(DocumentStatsContract.VALUE, documentStats.isValue());
        return values;
    }

    static Member buildMember(Cursor cursor) {
        return Member.builder()
                .id(cursor.getInt(cursor.getColumnIndex(MemberContract._ID)))
                .name(cursor.getString(cursor.getColumnIndex(MemberContract.NAME)))
                .externalId(cursor.getString(cursor.getColumnIndex(MemberContract.EXTERNAL_ID)))
                .address(cursor.getString(cursor.getColumnIndex(MemberContract.ADDRESS)))
                .phone(cursor.getString(cursor.getColumnIndex(MemberContract.PHONE)))
                .qrcode(cursor.getString(cursor.getColumnIndex(MemberContract.QR_CODE)))
                .build();
    }

    static Track buildTrack(Cursor cursor) {
        return Track.builder()
                .id(cursor.getInt(cursor.getColumnIndex(TrackInfoContract._ID)))
                .externalId(cursor.getString(cursor.getColumnIndex(TrackInfoContract.EXTERNAL_ID)))
                .date(cursor.getString(cursor.getColumnIndex(TrackInfoContract.DATE)))
                .name(cursor.getString(cursor.getColumnIndex(TrackInfoContract.NAME)))
                .build();
    }

    static Visit buildVisit(Cursor cursor) {
        return Visit.builder()
                .id(cursor.getInt(cursor.getColumnIndex(VisitContract._ID)))
                .memberId(cursor.getInt(cursor.getColumnIndex(VisitContract.MEMBER_ID)))
                .date(new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(VisitContract.DATE)))))
                .build();
    }

    static TrackMember buildTrackMember(Cursor cursor) {
        return TrackMember.builder()
                .id(cursor.getInt(cursor.getColumnIndex(TrackMemberContract._ID)))
                .memberExternalId(cursor.getString(cursor.getColumnIndex(TrackMemberContract.MEMBER_EXTERNAL_ID)))
                .trackExternalId(cursor.getString(cursor.getColumnIndex(TrackMemberContract.TRACK_EXTERNAL_ID)))
                .build();
    }

    static Document buildDocument(Cursor cursor) {
        return Document.builder()
                .id(cursor.getInt(cursor.getColumnIndex(DocumentContract._ID)))
                .visitId(cursor.getInt(cursor.getColumnIndex(DocumentContract.VISIT_ID)))
                .code(cursor.getString(cursor.getColumnIndex(DocumentContract.CODE)))
                .value(cursor.getInt(cursor.getColumnIndex(DocumentContract.VALUE)) == 1)
                .build();
    }

    static ServiceDemand buildServiceDemand(Cursor cursor) {
        return ServiceDemand.builder()
                .id(cursor.getInt(cursor.getColumnIndex(ServiceDemandContract._ID)))
                .visitId(cursor.getInt(cursor.getColumnIndex(ServiceDemandContract.VISIT_ID)))
                .code(cursor.getString(cursor.getColumnIndex(ServiceDemandContract.CODE)))
                .value(cursor.getInt(cursor.getColumnIndex(ServiceDemandContract.VALUE)) == 1)
                .build();
    }

    static ServiceDelivery buildServiceDelivery(Cursor cursor) {
        return ServiceDelivery.builder()
                .id(cursor.getInt(cursor.getColumnIndex(ServiceDeliveryContract._ID)))
                .trackMemberId(cursor.getInt(cursor.getColumnIndex(ServiceDeliveryContract.TRACK_MEMBER_ID)))
                .code(cursor.getString(cursor.getColumnIndex(ServiceDeliveryContract.CODE)))
                .value(cursor.getInt(cursor.getColumnIndex(ServiceDeliveryContract.VALUE)) == 1)
                .build();
    }

    static ContentValues convertCode(String externalId, String name) {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(EXTERNAL_ID, externalId);
        return values;
    }

    static ContentValues convertTrackMember(TrackMember trackMember) {
        ContentValues content = new ContentValues();
        content.put(TrackMemberContract.TRACK_EXTERNAL_ID, trackMember.getTrackExternalId());
        content.put(TrackMemberContract.MEMBER_EXTERNAL_ID, trackMember.getMemberExternalId());
        content.put(TrackMemberContract.MEMBER_CHANGED, false);
        return content;
    }

    static ContentValues convertMilkParam(MilkParam milkParam) {
        ContentValues values = new ContentValues();
        values.put(MilkParamsContract.VISIT_ID, milkParam.getVisitId());
        values.put(MilkParamsContract.FAT, milkParam.getFat());
        values.put(MilkParamsContract.SNF, milkParam.getSnf());
        values.put(MilkParamsContract.DENCITY, milkParam.getDencity());
        values.put(MilkParamsContract.ADDED_WATER, milkParam.getAddedWater());
        values.put(MilkParamsContract.FP, milkParam.getFp());
        values.put(MilkParamsContract.PROTEIN, milkParam.getProtein());
        values.put(MilkParamsContract.CONDUCTIVITY, milkParam.getConductivity());
        values.put(MilkParamsContract.VOLUME, milkParam.getVolume());
        values.put(MilkParamsContract.EKOMILK, milkParam.isEkomilk());
        return values;
    }

    static ContentValues convertTrackInfo(Track track) {
        ContentValues values = new ContentValues();
        values.put(TrackInfoContract.EXTERNAL_ID, track.getExternalId());
        values.put(TrackInfoContract.NAME, track.getName());
        values.put(TrackInfoContract.DATE, track.getDate());
        return values;
    }

    static ContentValues convertService(ServiceDelivery serviceDelivery) {
        ContentValues value = new ContentValues();
        value.put(ServiceDeliveryContract.TRACK_MEMBER_ID, serviceDelivery.getTrackMemberId());
        value.put(ServiceDeliveryContract.CODE, serviceDelivery.getCode());
        value.put(ServiceDeliveryContract.VALUE, serviceDelivery.getValue());
        return value;
    }

    static ContentValues convertServiceCodes(String externalId, String name, String parentId) {
        ContentValues values = new ContentValues();
        values.put(CooperativeContract.ServiceCodeContract.NAME, name);
        values.put(CooperativeContract.ServiceCodeContract.EXTERNAL_ID, externalId);
        values.put(CooperativeContract.ServiceCodeContract.PARENT_ID, parentId);
        return values;
    }
}
