package com.example.women_safeguard;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.women_safeguard.database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BatteryMonitorService extends Service {
    private BroadcastReceiver batteryReceiver;
    private SharedPreferences sharedPreferences;
    private boolean isMessageSent = false;
    private List<String> emergencyContacts = new ArrayList<>();
    private static final String CHANNEL_ID = "battery_alerts";

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("BatterySettings", MODE_PRIVATE);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        // Create the notification channel
        createNotificationChannel();

        // Fetch emergency contacts from Firebase
        fetchEmergencyContacts();

        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int threshold = sharedPreferences.getInt("batteryThreshold", 15);
                boolean alertsEnabled = sharedPreferences.getBoolean("alertsEnabled", false);

                Log.d("DEBUG", "Battery level: " + level + ", Threshold: " + threshold + ", Alerts enabled: " + alertsEnabled);

                if (alertsEnabled && level <= threshold && !isMessageSent) {
                    Log.d("DEBUG", "Sending emergency messages");
                    sendEmergencyMessages(level);
                    isMessageSent = true;
                }

                if (level > threshold) {
                    isMessageSent = false;
                }
            }
        };

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Battery Alerts";
            String description = "Channel for battery alert notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void fetchEmergencyContacts() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        if (currentUserId == null) {
            Log.e("USER_ID_ERROR", "Current user ID is not available");
            return;
        }

        Log.d("DEBUG", "Fetching emergency contacts for user ID: " + currentUserId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emergencyContacts.clear();
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    addValidContact(user.emergency1);
                    addValidContact(user.emergency2);
                    addValidContact(user.emergency3);
                } else {
                    Log.e("USER_DATA_ERROR", "User data is null for user ID: " + currentUserId);
                }

                Log.d("DEBUG", "Emergency contacts fetched: " + emergencyContacts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FIREBASE_ERROR", "Failed to read emergency contacts", databaseError.toException());
            }
        });
    }

    private void addValidContact(String contact) {
        if (contact != null && contact.matches("^\\+?\\d{10,13}$")) { // Simple regex to validate phone number
            emergencyContacts.add(contact);
            Log.d("DEBUG", "Valid contact added: " + contact);
        } else {
            Log.w("INVALID_CONTACT", "Invalid contact number: " + contact);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "RESET_MESSAGE_FLAG".equals(intent.getAction())) {
            isMessageSent = false;
            showNotification("Messages Stopped", "Emergency message sending has been reset");
        }
        return START_STICKY;
    }

    private void sendEmergencyMessages(int batteryLevel) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String locationStr = "Location unavailable";

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    locationStr = "http://maps.google.com/?q=" +
                            lastLocation.getLatitude() + "," +
                            lastLocation.getLongitude();
                }
            }
        } catch (Exception e) {
            Log.e("LOCATION_ERROR", "Failed to get location", e);
        }

        String message = String.format(
                "EMERGENCY ALERT: My phone battery is at %d%% and might switch off soon. " +
                        "My current location: %s",
                batteryLevel,
                locationStr
        );

        SmsManager smsManager = SmsManager.getDefault();
        for (String contact : emergencyContacts) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                try {
                    smsManager.sendTextMessage(contact, null, message, null, null);
                    Log.d("DEBUG", "SMS sent to: " + contact);
                } catch (Exception e) {
                    Log.e("SMS_SEND_ERROR", "Failed to send SMS to " + contact, e);
                }
            } else {
                Log.e("PERMISSION_ERROR", "SMS permission not granted");
            }
        }

        showNotification("Emergency Alert Sent", "Battery alert messages sent to emergency contacts");
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        try {
            notificationManager.notify(1, builder.build());
        } catch (Exception e) {
            Log.e("NOTIFICATION_ERROR", "Failed to show notification", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }
}