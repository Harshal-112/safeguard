package com.example.women_safeguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences("BatterySettings", Context.MODE_PRIVATE);

            if (sharedPreferences.getBoolean("alertsEnabled", false)) {
                Intent serviceIntent = new Intent(context, BatteryMonitorService.class);
                context.startService(serviceIntent);
            }
        }
    }
}
