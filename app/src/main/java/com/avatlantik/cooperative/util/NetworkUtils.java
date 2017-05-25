package com.avatlantik.cooperative.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.util.Properties;

import static com.avatlantik.cooperative.common.Consts.APPLICATION_PROPERTIES;

public class NetworkUtils {
    private static Context context;

    public NetworkUtils(Context context) {
        this.context = context;
    }

    public static boolean checkEthernet() {

        final ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean WIFISwitch() {

        final ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkWIFIconnectionToEcomilk() {

        String ssid = "";
        try {
            Properties properties = PropertyUtils.getProperties(APPLICATION_PROPERTIES, context);
            ssid = properties.getProperty("wifiecomilkid");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read auth properties", e);
        }

        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null) {
            return wifiInfo.getSSID().equals("\"" + ssid + "\"");
        }

        return false;

    }

    public static boolean checkUSBconnectionToEcomilk() {

        Intent intent = context.registerReceiver(null, new IntentFilter("android.hardware.usb.action.USB_STATE"));
        return intent.getExtras().getBoolean("connected");

    }
}
