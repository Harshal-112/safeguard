package com.example.women_safeguard;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.d("SMSReceiver", "SMS sent successfully!");
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Log.d("SMSReceiver", "Generic failure in sending SMS.");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Log.d("SMSReceiver", "No service available.");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Log.d("SMSReceiver", "Null PDU error.");
                break;
        }

        if (intent.getAction().equals("SMS_DELIVERED")) {
            Log.d("SMSReceiver", "SMS delivered!");
        }
    }
}
