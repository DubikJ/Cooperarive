package com.avatlantik.cooperative.util;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.avatlantik.cooperative.R;

import static com.avatlantik.cooperative.common.Consts.TAGLOG_PHONE;


public class PhoneUtils {


    public PhoneUtils() {
    }

    public static void sendSMS(Context contextActivity, String phoneNumber, String message) {
        final Context context = contextActivity;
        final String SENT = "SMS_SENT";
        final String DELIVERED = "SMS_DELIVERED";

        phoneNumber = getCorrectPhoneNumber(phoneNumber);

        if(phoneNumber == null && phoneNumber.isEmpty()){
            Toast.makeText(context, context.getResources().getString(R.string.phone_number_not_found),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);

        //---когда SMS отправлено---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, context.getResources().getString(R.string.phone_sms_send),
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAGLOG_PHONE, "Sms send");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, context.getResources().getString(R.string.phone_sms_dont_send),
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAGLOG_PHONE, "Generic failure");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, context.getResources().getString(R.string.phone_sms_dont_send),
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAGLOG_PHONE, "No service");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, context.getResources().getString(R.string.phone_sms_dont_send),
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAGLOG_PHONE, "Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, context.getResources().getString(R.string.phone_sms_dont_send),
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAGLOG_PHONE, "Radio off");
                        break;
                }
            }
        }, new IntentFilter(SENT));


        //---когда SMS доставлено---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, context.getResources().getString(R.string.phone_sms_delivered),
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAGLOG_PHONE, "SMS delivered");
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, context.getResources().getString(R.string.phone_sms_not_delivered),
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAGLOG_PHONE, "SMS not delivered");
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    public static void call(Context contextActivity, String phoneNumber) {
        final Context context = contextActivity;

        phoneNumber = getCorrectPhoneNumber(phoneNumber);

        if(phoneNumber == null && phoneNumber.isEmpty()){
            Toast.makeText(context, context.getResources().getString(R.string.phone_number_not_found),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        context.startActivity(intent);

    }



    private static String getCorrectPhoneNumber(String phoneNumberIn){

        String phoneNumberOut = "";

        String phoneNumberArr[] = phoneNumberIn.split("");
        for (int i = 0; i < phoneNumberArr.length; i++) {
            try {
                int number = Integer.parseInt(phoneNumberArr[i]);
                phoneNumberOut = phoneNumberOut + number;
            } catch (NumberFormatException e) {
            }
        }

        return phoneNumberOut;
    }

}
