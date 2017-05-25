package com.avatlantik.cooperative.common;

public final class Consts {
    public final static String TAGLOG = "Cooperative";
    public final static String TAGLOG_PHONE = "Cooperative_Phone";

    public final static String LOGIN = "mLogin";
    public final static String PASSWORD = "mPassword";
    public final static String SERVER = "mServer";
    public final static String EKOMILK_CONNECTION_TYPE = "ekomilkConnectionType";
    public final static String EKOMILK_USING = "ekomilkUsing";
    public final static String FLOWMETER_USING = "flowmeterUsing";
    public final static String PHONE_USING = "phoneUsing";
    public final static String EKOMILK_AUTOMATIC_START = "ekomilkAutoStart";

    public final static String ROOT_DIR = "cooperative";

    public final static String SETTINGSACTIVITYLOGIN = "settingsActivityLogin";
    public final static int STARTACTIVITYLOGIN = 0;
    public final static int STARTACTIVITYLANDING = 1;
    public final static int EXITAPPLICATION = 2;

    // Settings
    public final static int MINIMUM_NUMBERS_DAYS_VISITS = 7;
    public final static boolean CLEAR_DATABASE_IN_LOGOUT = true;
    public final static int CONNECT_TIMEOUT_SECONDS_RETROFIT = 180;

    // Synchronization
    public final static int STATUS_STARTED_SYNC = 0;
    public final static int STATUS_FINISHED_SYNC = 1;
    public final static int STATUS_ERROR_SYNC = -1;

    // Milk Params
    public final static String MILK_FAT = "mFat";
    public final static String MILK_SNF = "mSnf";
    public final static String MILK_DENCITY = "mDencity";
    public final static String MILK_ADDEDWATER = "mAddedWater";
    public final static String MILK_FP = "mFp";
    public final static String MILK_PROTEIN = "mProtein";
    public final static String MILK_CONDUCTIVITY = "mConductivity";
    public final static String MILK_VOLUME = "mVolume";
    public final static String ECOMILK_SYNC = "mEcomilkSync";
    public final static String EKOMILK_DEVICE_TYPE = "ekomilkDeviceType";

    //Ecomilk commands
    public final static String EKOMILK_START_ANALYSE = "com.android.ECOMILK_START_ANALYSE";
    public final static String EKOMILK_START_AUTO_ANALYSE = "com.android.ECOMILK_START_AUTO_ANALYSE";
    public final static String FLOWMETER_START_ANALYSE = "com.android.FLOWMETER_START_ANALYSE";

    public static final String APPLICATION_PROPERTIES = "application.properties";

    public static final String CLEAR_GUID = "00000000-0000-0000-0000-000000000000";

}
