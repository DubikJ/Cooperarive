package com.avatlantik.cooperative.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.avatlantik.cooperative.db.CooperativeContract.DocumentContract;

import static com.avatlantik.cooperative.db.CooperativeContract.DocumentCodeContract;
import static com.avatlantik.cooperative.db.CooperativeContract.DocumentStatsContract;
import static com.avatlantik.cooperative.db.CooperativeContract.MemberContract;
import static com.avatlantik.cooperative.db.CooperativeContract.MemberStatsContract;
import static com.avatlantik.cooperative.db.CooperativeContract.MilkParamsContract;
import static com.avatlantik.cooperative.db.CooperativeContract.MilkStatsContract;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceCodeContract;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceDeliveryContract;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceDemandContract;
import static com.avatlantik.cooperative.db.CooperativeContract.TrackInfoContract;
import static com.avatlantik.cooperative.db.CooperativeContract.TrackMemberContract;
import static com.avatlantik.cooperative.db.CooperativeContract.VisitContract;
import static com.avatlantik.cooperative.db.CooperativeContract.UserSettings;

public class CooperativeDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "cooperative";
    private static final int DB_VERSION = 24;

    public CooperativeDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MemberContract.TABLE_NAME + "("
                + MemberContract._ID + " integer primary key AUTOINCREMENT,"
                + MemberContract.EXTERNAL_ID + " text,"
                + MemberContract.QR_CODE + " text,"
                + MemberContract.NAME + " text,"
                + MemberContract.ADDRESS + " text,"
                + MemberContract.PHONE + " text,"
                + "UNIQUE (" + TextUtils.join(", ", MemberContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + TrackInfoContract.TABLE_NAME + "("
                + TrackInfoContract._ID + " integer primary key AUTOINCREMENT,"
                + TrackInfoContract.EXTERNAL_ID + " text,"
                + TrackInfoContract.NAME + " text,"
                + TrackInfoContract.DATE + " text,"
                + "UNIQUE (" + TextUtils.join(", ", TrackInfoContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + TrackMemberContract.TABLE_NAME + "("
                + TrackMemberContract._ID + " integer primary key AUTOINCREMENT,"
                + TrackMemberContract.TRACK_EXTERNAL_ID + " text,"
                + TrackMemberContract.MEMBER_EXTERNAL_ID + " text,"
                + TrackMemberContract.MEMBER_CHANGED + " numeric,"
                + "UNIQUE (" + TextUtils.join(", ", TrackMemberContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + ServiceDeliveryContract.TABLE_NAME + "("
                + ServiceDeliveryContract._ID + " integer primary key AUTOINCREMENT,"
                + ServiceDeliveryContract.TRACK_MEMBER_ID + " integer,"
                + ServiceDeliveryContract.CODE + " text,"
                + ServiceDeliveryContract.VALUE + " numeric,"
                + "UNIQUE (" + TextUtils.join(", ", ServiceDeliveryContract.UNIQUE_COLUMNS) + ")"
                + ");");


        db.execSQL("create table " + ServiceDemandContract.TABLE_NAME + "("
                + ServiceDemandContract._ID + " integer primary key AUTOINCREMENT,"
                + ServiceDemandContract.VISIT_ID + " integer,"
                + ServiceDemandContract.CODE + " text,"
                + ServiceDemandContract.VALUE + " numeric,"
                + "UNIQUE (" + TextUtils.join(", ", ServiceDemandContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + VisitContract.TABLE_NAME + "("
                + VisitContract._ID + " integer primary key AUTOINCREMENT,"
                + VisitContract.MEMBER_ID + " integer,"
                + VisitContract.DATE + " integer, "
                + "UNIQUE (" + TextUtils.join(", ", VisitContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + ServiceCodeContract.TABLE_NAME + "("
                + ServiceCodeContract._ID + " integer primary key AUTOINCREMENT,"
                + ServiceCodeContract.NAME + " text,"
                + ServiceCodeContract.EXTERNAL_ID + " text,"
                + ServiceCodeContract.PARENT_ID + " text,"
                + "UNIQUE (" + TextUtils.join(", ", ServiceCodeContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + MilkParamsContract.TABLE_NAME + "("
                + MilkParamsContract._ID + " integer primary key AUTOINCREMENT,"
                + MilkParamsContract.VISIT_ID + " integer,"
                + MilkStatsContract.FAT + " real,"
                + MilkStatsContract.SNF + " real,"
                + MilkStatsContract.DENCITY + " real,"
                + MilkStatsContract.ADDED_WATER + " real,"
                + MilkStatsContract.FP + " real,"
                + MilkStatsContract.PROTEIN + " real,"
                + MilkStatsContract.CONDUCTIVITY + " real,"
                + MilkStatsContract.VOLUME + " real,"
                + MilkParamsContract.EKOMILK + " numeric,"
                + "UNIQUE (" + TextUtils.join(", ", MilkParamsContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + MemberStatsContract.TABLE_NAME + "("
                + MemberStatsContract._ID + " integer primary key AUTOINCREMENT,"
                + MemberStatsContract.MEMBER_ID + " integer,"
                + MemberStatsContract.COMPANY_LOAN + " real,"
                + MemberStatsContract.MEMBER_LOAN + " real,"
                + MemberStatsContract.MILK_VOLUME + " real,"
                + "UNIQUE (" + TextUtils.join(", ", MemberStatsContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + MilkStatsContract.TABLE_NAME + "("
                + MilkStatsContract._ID + " integer primary key AUTOINCREMENT,"
                + MilkStatsContract.MEMBER_ID + " text,"
                + MilkStatsContract.FAT + " real,"
                + MilkStatsContract.SNF + " real,"
                + MilkStatsContract.DENCITY + " real,"
                + MilkStatsContract.ADDED_WATER + " real,"
                + MilkStatsContract.FP + " real,"
                + MilkStatsContract.PROTEIN + " real,"
                + MilkStatsContract.CONDUCTIVITY + " real,"
                + MilkStatsContract.VOLUME + " real,"
                + "UNIQUE (" + TextUtils.join(", ", MilkStatsContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + DocumentStatsContract.TABLE_NAME + "("
                + DocumentStatsContract._ID + " integer primary key AUTOINCREMENT,"
                + DocumentStatsContract.MEMBER_ID + " text,"
                + DocumentStatsContract.CODE + " text,"
                + DocumentStatsContract.VALUE + " numeric,"
                + "UNIQUE (" + TextUtils.join(", ", DocumentStatsContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + DocumentContract.TABLE_NAME + "("
                + DocumentContract._ID + " integer primary key AUTOINCREMENT,"
                + DocumentContract.VISIT_ID + " integer,"
                + DocumentContract.CODE + " text,"
                + DocumentContract.VALUE + " numeric,"
                + "UNIQUE (" + TextUtils.join(", ", DocumentContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + DocumentCodeContract.TABLE_NAME + "("
                + DocumentCodeContract._ID + " integer primary key AUTOINCREMENT,"
                + DocumentCodeContract.NAME + " text,"
                + DocumentCodeContract.EXTERNAL_ID + " text,"
                + "UNIQUE (" + TextUtils.join(",", DocumentCodeContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + UserSettings.TABLE_NAME + "("
                + UserSettings._ID + " integer primary key AUTOINCREMENT,"
                + UserSettings.USER_SETTING_ID + " text,"
                + UserSettings.SETTING_VALUE + " text,"
                + "UNIQUE (" + TextUtils.join(",", UserSettings.UNIQUE_COLUMNS) + ")"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + MemberContract.TABLE_NAME);
        db.execSQL("drop table if exists " + TrackInfoContract.TABLE_NAME);
        db.execSQL("drop table if exists " + TrackMemberContract.TABLE_NAME);
        db.execSQL("drop table if exists " + VisitContract.TABLE_NAME);
        db.execSQL("drop table if exists " + ServiceDeliveryContract.TABLE_NAME);
        db.execSQL("drop table if exists " + ServiceDemandContract.TABLE_NAME);
        db.execSQL("drop table if exists " + DocumentContract.TABLE_NAME);
        db.execSQL("drop table if exists " + ServiceCodeContract.TABLE_NAME);
        db.execSQL("drop table if exists " + DocumentCodeContract.TABLE_NAME);
        db.execSQL("drop table if exists " + MilkParamsContract.TABLE_NAME);
        db.execSQL("drop table if exists " + MilkStatsContract.TABLE_NAME);
        db.execSQL("drop table if exists " + MemberStatsContract.TABLE_NAME);
        db.execSQL("drop table if exists " + DocumentStatsContract.TABLE_NAME);
        db.execSQL("drop table if exists " + UserSettings.TABLE_NAME);

        onCreate(db);
    }
}
