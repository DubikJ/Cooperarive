package com.avatlantik.cooperative.db;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class CooperativeContract {

    private static final String CONTENT = "content://";
    public static final String AUTHORITY = "com.avatlantik.cooperative.dbProvider";

    public static final Uri CONTENT_URI
            = Uri.parse(CONTENT + AUTHORITY);


    public static final class MemberContract implements BaseColumns {

        public static final String TABLE_NAME = "member";

        public static final String EXTERNAL_ID = "external_id";
        public static final String QR_CODE = "qrCode";
        public static final String NAME = "name";
        public static final String ADDRESS = "address";
        public static final String PHONE = "phone";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, EXTERNAL_ID, QR_CODE, NAME, ADDRESS, PHONE};

        public static final String[] UNIQUE_COLUMNS =
                {EXTERNAL_ID};

        public static final String DEFAULT_SORT_ORDER = NAME + " ASC";
    }

    public static final class TrackInfoContract implements BaseColumns {

        public static final String TABLE_NAME = "track_info";

        public static final String EXTERNAL_ID = "external_id";
        public static final String NAME = "name";
        public static final String DATE = "date";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, EXTERNAL_ID, NAME, DATE};

        public static final String[] UNIQUE_COLUMNS =
                {EXTERNAL_ID};

        public static final String DEFAULT_SORT_ORDER = NAME + " ASC";
    }

    public static final class TrackMemberContract implements BaseColumns {

        public static final String TABLE_NAME = "track_member";

        public static final String TRACK_EXTERNAL_ID = "external_id";
        public static final String MEMBER_EXTERNAL_ID = "member_external_id";
        public static final String MEMBER_CHANGED = "member_changed";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, TRACK_EXTERNAL_ID, MEMBER_EXTERNAL_ID};

        public static final String[] UNIQUE_COLUMNS =
                {TRACK_EXTERNAL_ID, MEMBER_EXTERNAL_ID, MEMBER_CHANGED};

        public static final String DEFAULT_SORT_ORDER = MEMBER_EXTERNAL_ID + " ASC";
    }

    public static final class VisitContract implements BaseColumns {

        public static final String TABLE_NAME = "visit";

        public static final String MEMBER_ID = "member_id";
        public static final String DATE = "date";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, MEMBER_ID, DATE};

        public static final String[] UNIQUE_COLUMNS =
                {MEMBER_ID, DATE};


        public static final String DEFAULT_SORT_ORDER = DATE + " ASC";

    }

    public static final class ServiceDeliveryContract implements BaseColumns {

        public static final String TABLE_NAME = "service_delivery";

        public static final String TRACK_MEMBER_ID = "track_member_id";
        public static final String CODE = "code";
        public static final String VALUE = "value";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, TRACK_MEMBER_ID, CODE, VALUE};

        public static final String[] UNIQUE_COLUMNS =
                {TRACK_MEMBER_ID, CODE};

        public static final String DEFAULT_SORT_ORDER = VALUE + " ASC";

    }

    public static final class ServiceDemandContract implements BaseColumns {

        public static final String TABLE_NAME = "service_demand";

        public static final String VISIT_ID = "visit_id";
        public static final String CODE = "code";
        public static final String VALUE = "value";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, VISIT_ID, CODE, VALUE};

        public static final String[] UNIQUE_COLUMNS =
                {VISIT_ID, CODE};

        public static final String DEFAULT_SORT_ORDER = VALUE + " ASC";

    }

    public static final class ServiceCodeContract implements BaseColumns {

        public static final String TABLE_NAME = "service_codes";

        public static final String NAME = "name";
        public static final String EXTERNAL_ID = "external_id";
        public static final String PARENT_ID = "parent_id";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, NAME, EXTERNAL_ID, PARENT_ID};

        public static final String[] UNIQUE_COLUMNS = {EXTERNAL_ID};

        public static final String DEFAULT_SORT_ORDER = NAME + " ASC";
    }

    public static final class DocumentContract implements BaseColumns {

        public static final String TABLE_NAME = "documents";

        public static final String VISIT_ID = "visit_id";
        public static final String CODE = "code";
        public static final String VALUE = "value";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, VISIT_ID, CODE, VALUE};

        public static final String[] UNIQUE_COLUMNS =
                {VISIT_ID, CODE};

        public static final String DEFAULT_SORT_ORDER = VALUE + " ASC";
    }


    public static final class DocumentCodeContract implements BaseColumns {

        public static final String TABLE_NAME = "document_codes";

        public static final String NAME = "name";
        public static final String EXTERNAL_ID = "external_id";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, EXTERNAL_ID, NAME};

        public static final String[] UNIQUE_COLUMNS =
                {EXTERNAL_ID};

        public static final String DEFAULT_SORT_ORDER = NAME + " ASC";
    }

    public static final class MilkParamsContract implements BaseColumns {

        public static final String TABLE_NAME = "milk_params";

        public static final String VISIT_ID = "visit_id";
        public static final String FAT = "fat";
        public static final String SNF = "snf";
        public static final String DENCITY = "dencity";
        public static final String ADDED_WATER = "added_water";
        public static final String FP = "fp";
        public static final String PROTEIN = "protein";
        public static final String CONDUCTIVITY = "conductivity";
        public static final String VOLUME = "volume";
        public static final String EKOMILK = "ekomilk";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, VISIT_ID, FAT, SNF, DENCITY, ADDED_WATER, FP, PROTEIN, CONDUCTIVITY, VOLUME, EKOMILK};

        public static final String[] UNIQUE_COLUMNS =
                {VISIT_ID};

        public static final String DEFAULT_SORT_ORDER = VOLUME + " ASC";
    }

    public static final class MilkStatsContract implements BaseColumns {

        public static final String TABLE_NAME = "milk_stats";

        public static final String MEMBER_ID = "member_id";
        public static final String FAT = "fat";
        public static final String SNF = "snf";
        public static final String DENCITY = "dencity";
        public static final String ADDED_WATER = "added_water";
        public static final String FP = "fp";
        public static final String PROTEIN = "protein";
        public static final String CONDUCTIVITY = "conductivity";
        public static final String VOLUME = "volume";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] UNIQUE_COLUMNS =
                {MEMBER_ID};

        public static final String[] PROJECTION_ALL =
                {_ID, MEMBER_ID, FAT, SNF, DENCITY, ADDED_WATER, FP, PROTEIN, CONDUCTIVITY, VOLUME};

        public static final String DEFAULT_SORT_ORDER = VOLUME + " ASC";
    }

    public static final class MemberStatsContract implements BaseColumns {

        public static final String TABLE_NAME = "member_stats";

        public static final String MEMBER_ID = "member_id";
        public static final String COMPANY_LOAN = "company_loan";
        public static final String MEMBER_LOAN = "member_loan";
        public static final String MILK_VOLUME = "milk_volume";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] UNIQUE_COLUMNS =
                {MEMBER_ID};


        public static final String[] PROJECTION_ALL =
                {_ID, MEMBER_ID, COMPANY_LOAN, MEMBER_LOAN, MILK_VOLUME};

        public static final String DEFAULT_SORT_ORDER = COMPANY_LOAN + " ASC";

    }

    public static final class DocumentStatsContract implements BaseColumns {

        public static final String TABLE_NAME = "document_stats";

        public static final String MEMBER_ID = "member_id";
        public static final String CODE = "code";
        public static final String VALUE = "value";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, MEMBER_ID, CODE, VALUE};

        public static final String[] UNIQUE_COLUMNS =
                {MEMBER_ID, CODE};

        public static final String DEFAULT_SORT_ORDER = VALUE + " ASC";

    }

    public static final class UserSettings implements BaseColumns {

        public static final String TABLE_NAME = "user_settings";

        public static final String USER_SETTING_ID = "setting_id";
        public static final String SETTING_VALUE = "value";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        CooperativeContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, USER_SETTING_ID, SETTING_VALUE};

        public static final String[] UNIQUE_COLUMNS =
                {USER_SETTING_ID};

        public static final String DEFAULT_SORT_ORDER = SETTING_VALUE + " ASC";

    }

}
