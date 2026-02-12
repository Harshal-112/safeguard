package com.example.women_safeguard;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class SendMultipleSMSWithReports {

    public static void sendSmsToMultipleNumbersWithReports(Context context, String[] phoneNumbers, String message) {
        // Get the default SmsManager instance
        SmsManager smsManager = SmsManager.getDefault();

        // Loop through the list of phone numbers and send the SMS to each number
        for (String phoneNumber : phoneNumbers) {
            // Create intents for sent and delivered SMS reports
            Intent sentIntent = new Intent("SMS_SENT");
            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(context, 0, sentIntent, 0);

            Intent deliveryIntent = new Intent("SMS_DELIVERED");
            PendingIntent deliveryPendingIntent = PendingIntent.getBroadcast(context, 0, deliveryIntent, 0);

            // Send the SMS
            smsManager.sendTextMessage(phoneNumber, null, message, sentPendingIntent, deliveryPendingIntent);
        }
    }
}
