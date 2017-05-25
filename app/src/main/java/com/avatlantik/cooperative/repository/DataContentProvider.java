package com.avatlantik.cooperative.repository;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.avatlantik.cooperative.db.CooperativeContract.DocumentContract;
import com.avatlantik.cooperative.db.CooperativeContract.DocumentStatsContract;
import com.avatlantik.cooperative.db.CooperativeContract.MilkStatsContract;
import com.avatlantik.cooperative.db.CooperativeDb;

import java.util.ArrayList;
import java.util.List;

import static com.avatlantik.cooperative.db.CooperativeContract.AUTHORITY;
import static com.avatlantik.cooperative.db.CooperativeContract.DocumentCodeContract;
import static com.avatlantik.cooperative.db.CooperativeContract.MemberContract;
import static com.avatlantik.cooperative.db.CooperativeContract.MemberStatsContract;
import static com.avatlantik.cooperative.db.CooperativeContract.MilkParamsContract;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceCodeContract;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceDeliveryContract;
import static com.avatlantik.cooperative.db.CooperativeContract.ServiceDemandContract;
import static com.avatlantik.cooperative.db.CooperativeContract.TrackInfoContract;
import static com.avatlantik.cooperative.db.CooperativeContract.TrackMemberContract;
import static com.avatlantik.cooperative.db.CooperativeContract.UserSettings;
import static com.avatlantik.cooperative.db.CooperativeContract.VisitContract;

public class DataContentProvider extends ContentProvider {

    private CooperativeDb cooperativeDb;
    private final UriMatcher URI_MATCHER;

    private static final int MEMBERS_LIST = 1;
    private static final int MEMBER_ID = 2;
    private static final int VISIT_LIST = 3;
    private static final int VISIT_ID = 4;
    private static final int SERVICE_DELIVERY_LIST = 5;
    private static final int SERVICE_DELIVERY_ID = 6;
    private static final int SERVICE_CODE_LIST = 7;
    private static final int SERVICE_CODE_ID = 8;
    private static final int MILK_PARAMS_LIST = 9;
    private static final int MILK_PARAMS_ID = 10;
    private static final int MEMBER_STATS_LIST = 11;
    private static final int MEMBER_STATS_ID = 12;
    private static final int TRACK_INFO_LIST = 13;
    private static final int TRACK_INFO_ID = 14;
    private static final int DOCUMENT_CODE_LIST = 15;
    private static final int DOCUMENT_CODE_ID = 16;
    private static final int TRACK_LIST = 17;
    private static final int TRACK_ID = 18;
    private static final int SERVICE_DEMAND_LIST = 19;
    private static final int SERVICE_DEMAND_ID = 20;
    private static final int MILK_STATS_LIST = 21;
    private static final int MILK_STATS_ID = 22;
    private static final int DOCUMENT_LIST = 23;
    private static final int DOCUMENT_ID = 24;
    private static final int DOCUMENT_STATS_LIST = 25;
    private static final int DOCUMENT_STATS_ID = 26;
    private static final int USER_SETTING_LIST = 27;
    private static final int USER_SETTING_ID = 28;


    public DataContentProvider() {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, MemberContract.TABLE_NAME, MEMBERS_LIST);
        URI_MATCHER.addURI(AUTHORITY, MemberContract.TABLE_NAME + "/#", MEMBER_ID);
        URI_MATCHER.addURI(AUTHORITY, VisitContract.TABLE_NAME, VISIT_LIST);
        URI_MATCHER.addURI(AUTHORITY, VisitContract.TABLE_NAME + "/#", VISIT_ID);
        URI_MATCHER.addURI(AUTHORITY, ServiceDeliveryContract.TABLE_NAME, SERVICE_DELIVERY_LIST);
        URI_MATCHER.addURI(AUTHORITY, ServiceDeliveryContract.TABLE_NAME + "/#", SERVICE_DELIVERY_ID);
        URI_MATCHER.addURI(AUTHORITY, ServiceDemandContract.TABLE_NAME, SERVICE_DEMAND_LIST);
        URI_MATCHER.addURI(AUTHORITY, ServiceDemandContract.TABLE_NAME + "/#", SERVICE_DEMAND_ID);
        URI_MATCHER.addURI(AUTHORITY, ServiceCodeContract.TABLE_NAME, SERVICE_CODE_LIST);
        URI_MATCHER.addURI(AUTHORITY, ServiceCodeContract.TABLE_NAME + "/#", SERVICE_CODE_ID);
        URI_MATCHER.addURI(AUTHORITY, MilkParamsContract.TABLE_NAME, MILK_PARAMS_LIST);
        URI_MATCHER.addURI(AUTHORITY, MilkParamsContract.TABLE_NAME + "/#", MILK_PARAMS_ID);
        URI_MATCHER.addURI(AUTHORITY, MemberStatsContract.TABLE_NAME, MEMBER_STATS_LIST);
        URI_MATCHER.addURI(AUTHORITY, MemberStatsContract.TABLE_NAME + "/#", MEMBER_STATS_ID);
        URI_MATCHER.addURI(AUTHORITY, TrackInfoContract.TABLE_NAME, TRACK_INFO_LIST);
        URI_MATCHER.addURI(AUTHORITY, TrackInfoContract.TABLE_NAME + "/#", TRACK_INFO_ID);
        URI_MATCHER.addURI(AUTHORITY, TrackMemberContract.TABLE_NAME, TRACK_LIST);
        URI_MATCHER.addURI(AUTHORITY, TrackMemberContract.TABLE_NAME + "/#", TRACK_ID);
        URI_MATCHER.addURI(AUTHORITY, DocumentCodeContract.TABLE_NAME, DOCUMENT_CODE_LIST);
        URI_MATCHER.addURI(AUTHORITY, DocumentCodeContract.TABLE_NAME + "/#", DOCUMENT_CODE_ID);
        URI_MATCHER.addURI(AUTHORITY, DocumentContract.TABLE_NAME, DOCUMENT_LIST);
        URI_MATCHER.addURI(AUTHORITY, DocumentContract.TABLE_NAME + "/#", DOCUMENT_ID);
        URI_MATCHER.addURI(AUTHORITY, MilkStatsContract.TABLE_NAME, MILK_STATS_LIST);
        URI_MATCHER.addURI(AUTHORITY, MilkStatsContract.TABLE_NAME + "/#", MILK_STATS_ID);
        URI_MATCHER.addURI(AUTHORITY, DocumentStatsContract.TABLE_NAME, DOCUMENT_STATS_LIST);
        URI_MATCHER.addURI(AUTHORITY, DocumentStatsContract.TABLE_NAME + "/#", DOCUMENT_STATS_ID);
        URI_MATCHER.addURI(AUTHORITY, UserSettings.TABLE_NAME, USER_SETTING_LIST);
        URI_MATCHER.addURI(AUTHORITY, UserSettings.TABLE_NAME + "/#", USER_SETTING_ID);
    }

    @Override
    public boolean onCreate() {
        cooperativeDb = new CooperativeDb(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = cooperativeDb.getReadableDatabase();
        String table = getTable(uri);
        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = cooperativeDb.getWritableDatabase();
        String table = getTable(uri);

        String[] uniqueColumn = getUniqueColumn(uri);
        if (uniqueColumn != null) {
            String selection = buildSelection(uniqueColumn);
            String[] args = buildSelectionArgs(uniqueColumn, values);

            db.update(table, values, selection, args);
        }

        return db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1 ? uri : null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        String table = getTable(uri);

        SQLiteDatabase sqlDB = cooperativeDb.getWritableDatabase();
        sqlDB.beginTransaction();
        try {
            for (ContentValues cv : values) {

                String[] uniqueColumn = getUniqueColumn(uri);
                if (uniqueColumn != null) {
                    String selection = buildSelection(uniqueColumn);
                    String[] args = buildSelectionArgs(uniqueColumn, cv);

                    sqlDB.update(table, cv, selection, args);
                }

                sqlDB.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            }
            sqlDB.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            numInserted = values.length;
        } finally {
            sqlDB.endTransaction();
        }

        return numInserted;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleted = 0;

        String table = getTable(uri);
        String[] uniqueColumn = getUniqueColumn(uri);

        SQLiteDatabase sqlDB = cooperativeDb.getWritableDatabase();
        sqlDB.beginTransaction();
        try {
            if (uniqueColumn != null) {
                deleted = sqlDB.delete(table, selection, selectionArgs);
            }

            sqlDB.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            return deleted;
        } finally {
            sqlDB.endTransaction();
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updated = 0;

        String table = getTable(uri);
        String[] uniqueColumn = getUniqueColumn(uri);

        SQLiteDatabase sqlDB = cooperativeDb.getWritableDatabase();
        sqlDB.beginTransaction();
        try {
            if (uniqueColumn != null) {
                updated = sqlDB.update(table, values, selection, selectionArgs);
            }

            sqlDB.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            return updated;
        } finally {
            sqlDB.endTransaction();
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case MEMBERS_LIST:
                return MemberContract.CONTENT_TYPE;
            case MEMBER_ID:
                return MemberContract.CONTENT_ITEM_TYPE;
            case SERVICE_DELIVERY_LIST:
                return ServiceDeliveryContract.CONTENT_TYPE;
            case SERVICE_DELIVERY_ID:
                return ServiceDeliveryContract.CONTENT_ITEM_TYPE;
            case SERVICE_DEMAND_LIST:
                return ServiceDemandContract.CONTENT_TYPE;
            case SERVICE_DEMAND_ID:
                return ServiceDemandContract.CONTENT_ITEM_TYPE;
            case SERVICE_CODE_LIST:
                return ServiceCodeContract.CONTENT_TYPE;
            case SERVICE_CODE_ID:
                return ServiceCodeContract.CONTENT_ITEM_TYPE;
            case MEMBER_STATS_LIST:
                return MemberStatsContract.CONTENT_TYPE;
            case MEMBER_STATS_ID:
                return MemberStatsContract.CONTENT_ITEM_TYPE;
            case MILK_PARAMS_LIST:
                return MilkParamsContract.CONTENT_TYPE;
            case MILK_PARAMS_ID:
                return MilkParamsContract.CONTENT_ITEM_TYPE;
            case VISIT_LIST:
                return VisitContract.CONTENT_TYPE;
            case VISIT_ID:
                return VisitContract.CONTENT_ITEM_TYPE;
            case TRACK_INFO_LIST:
                return TrackInfoContract.CONTENT_TYPE;
            case TRACK_INFO_ID:
                return TrackInfoContract.CONTENT_ITEM_TYPE;
            case TRACK_LIST:
                return TrackMemberContract.CONTENT_TYPE;
            case TRACK_ID:
                return TrackMemberContract.CONTENT_ITEM_TYPE;
            case DOCUMENT_CODE_LIST:
                return DocumentCodeContract.CONTENT_TYPE;
            case DOCUMENT_CODE_ID:
                return DocumentCodeContract.CONTENT_ITEM_TYPE;
            case MILK_STATS_ID:
                return MilkStatsContract.CONTENT_ITEM_TYPE;
            case MILK_STATS_LIST:
                return MilkStatsContract.CONTENT_TYPE;
            case DOCUMENT_STATS_ID:
                return DocumentStatsContract.CONTENT_ITEM_TYPE;
            case DOCUMENT_STATS_LIST:
                return DocumentStatsContract.CONTENT_TYPE;
            case DOCUMENT_ID:
                return DocumentContract.CONTENT_ITEM_TYPE;
            case DOCUMENT_LIST:
                return DocumentContract.CONTENT_TYPE;
            case USER_SETTING_ID:
                return UserSettings.CONTENT_ITEM_TYPE;
            case USER_SETTING_LIST:
                return UserSettings.CONTENT_TYPE;

            default:
                throw new RuntimeException("Cannot identify uri " + uri.toString());
        }
    }

    private String[] buildSelectionArgs(String[] uniqueColumn, ContentValues values) {
        List<String> args = new ArrayList<>();
        for (String str : uniqueColumn) {
            args.add(values.getAsString(str));
        }

        return args.toArray(new String[args.size()]);
    }

    private String buildSelection(String[] uniqueColumn) {
        StringBuilder builder = new StringBuilder();
        for (String column : uniqueColumn) {
            builder.append(column).append("=? AND ");
        }

        return builder.substring(0, builder.lastIndexOf(" ") - 3);
    }

    private String getTable(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case MEMBER_ID:
            case MEMBERS_LIST:
                return MemberContract.TABLE_NAME;
            case TRACK_INFO_ID:
            case TRACK_INFO_LIST:
                return TrackInfoContract.TABLE_NAME;
            case TRACK_ID:
            case TRACK_LIST:
                return TrackMemberContract.TABLE_NAME;
            case SERVICE_DELIVERY_ID:
            case SERVICE_DELIVERY_LIST:
                return ServiceDeliveryContract.TABLE_NAME;
            case SERVICE_DEMAND_ID:
            case SERVICE_DEMAND_LIST:
                return ServiceDemandContract.TABLE_NAME;
            case SERVICE_CODE_ID:
            case SERVICE_CODE_LIST:
                return ServiceCodeContract.TABLE_NAME;
            case DOCUMENT_CODE_ID:
            case DOCUMENT_CODE_LIST:
                return DocumentCodeContract.TABLE_NAME;
            case MEMBER_STATS_ID:
            case MEMBER_STATS_LIST:
                return MemberStatsContract.TABLE_NAME;
            case MILK_STATS_ID:
            case MILK_STATS_LIST:
                return MilkStatsContract.TABLE_NAME;
            case DOCUMENT_STATS_ID:
            case DOCUMENT_STATS_LIST:
                return DocumentStatsContract.TABLE_NAME;
            case DOCUMENT_ID:
            case DOCUMENT_LIST:
                return DocumentContract.TABLE_NAME;
            case MILK_PARAMS_ID:
            case MILK_PARAMS_LIST:
                return MilkParamsContract.TABLE_NAME;
            case VISIT_ID:
            case VISIT_LIST:
                return VisitContract.TABLE_NAME;
            case USER_SETTING_ID:
            case USER_SETTING_LIST:
                return UserSettings.TABLE_NAME;
            default:
                throw new RuntimeException("Cannot identify uri " + uri.toString());
        }
    }

    private String[] getUniqueColumn(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case MEMBER_ID:
            case MEMBERS_LIST:
                return MemberContract.UNIQUE_COLUMNS;
            case TRACK_INFO_ID:
            case TRACK_INFO_LIST:
                return TrackInfoContract.UNIQUE_COLUMNS;
            case SERVICE_DELIVERY_ID:
            case SERVICE_DELIVERY_LIST:
                return ServiceDeliveryContract.UNIQUE_COLUMNS;
            case SERVICE_DEMAND_ID:
            case SERVICE_DEMAND_LIST:
                return ServiceDemandContract.UNIQUE_COLUMNS;
            case SERVICE_CODE_ID:
            case SERVICE_CODE_LIST:
                return ServiceCodeContract.UNIQUE_COLUMNS;
            case DOCUMENT_CODE_ID:
            case DOCUMENT_CODE_LIST:
                return DocumentCodeContract.UNIQUE_COLUMNS;
            case MILK_PARAMS_ID:
            case MILK_PARAMS_LIST:
                return MilkParamsContract.UNIQUE_COLUMNS;
            case MILK_STATS_ID:
            case MILK_STATS_LIST:
                return MilkStatsContract.UNIQUE_COLUMNS;
            case DOCUMENT_STATS_ID:
            case DOCUMENT_STATS_LIST:
                return DocumentStatsContract.UNIQUE_COLUMNS;
            case DOCUMENT_ID:
            case DOCUMENT_LIST:
                return DocumentContract.UNIQUE_COLUMNS;
            case TRACK_ID:
            case TRACK_LIST:
                return TrackMemberContract.UNIQUE_COLUMNS;
            case USER_SETTING_ID:
            case USER_SETTING_LIST:
                return UserSettings.UNIQUE_COLUMNS;
            case VISIT_ID:
            case VISIT_LIST:
            case MEMBER_STATS_ID:
            case MEMBER_STATS_LIST:
                return null;
            default:
                throw new RuntimeException("Cannot identify uri " + uri.toString());
        }
    }
}


