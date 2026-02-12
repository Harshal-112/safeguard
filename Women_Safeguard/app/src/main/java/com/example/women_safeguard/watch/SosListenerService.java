package com.example.women_safeguard.watch;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.women_safeguard.R;
import com.example.women_safeguard.database.User;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SosListenerService extends WearableListenerService {
    private static final String TAG = "SosListenerService";
    private static final String CHANNEL_ID = "sos_service_channel";
    private static final int NOTIFICATION_ID = 1001;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private List<String> emergencyContacts = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SosListenerService created");

        // Create notification channel for foreground service
        createNotificationChannel();

        // Start as foreground service to prevent system from killing it
        startForeground(NOTIFICATION_ID, createNotification());

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() != null) {
            userRef = firebaseDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());
        } else {
            Log.e(TAG, "User not authenticated");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SosListenerService started");
        return START_STICKY; // Service will be restarted if killed by system
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged triggered with " + dataEvents.getCount() + " events");

        for (DataEvent event : dataEvents) {
            Log.d(TAG, "Event type: " + event.getType() + ", Path: " + event.getDataItem().getUri().getPath());

            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/sos_trigger")) {

                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                boolean sosActivated = dataMap.getBoolean("sos_activated", false);

                if (sosActivated) {
                    Log.d(TAG, "SOS signal received from watch");
                    // Process SOS request
                    fetchEmergencyContactsAndSendSms();
                }
            }
        }
    }

    private void fetchEmergencyContactsAndSendSms() {
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (currentUserId == null) {
            Log.e(TAG, "User not logged in");
            return;
        }

        Log.d(TAG, "Fetching emergency contacts for user ID: " + currentUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emergencyContacts.clear();
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    addValidContact(user.emergency1);
                    addValidContact(user.emergency2);
                    addValidContact(user.emergency3);

                    Log.d(TAG, "Emergency contacts fetched: " + emergencyContacts);
                    sendEmergencyMessages();
                    storeEmergencyEvent(currentUserId);
                } else {
                    Log.e(TAG, "User data not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read emergency contacts", databaseError.toException());
            }
        });
    }

    private void addValidContact(String contact) {
        if (contact != null && !contact.isEmpty() && contact.matches("^\\+?\\d{10,13}$")) {
            emergencyContacts.add(contact);
            Log.d(TAG, "Valid contact added: " + contact);
        } else {
            Log.w(TAG, "Invalid contact number: " + contact);
        }
    }

    private void sendEmergencyMessages() {
        if (emergencyContacts.isEmpty()) {
            Log.e(TAG, "No emergency contacts available.");
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String locationStr = "Location unavailable";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    locationStr = "http://maps.google.com/?q=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude();
                } else {
                    Log.w(TAG, "No recent location from GPS, trying network provider...");
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (lastLocation != null) {
                        locationStr = "http://maps.google.com/?q=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting location", e);
            }
        } else {
            Log.e(TAG, "Location permission not granted");
        }

        String message = "SOS! I need help. My location: " + locationStr;
        Log.d(TAG, "Sending message: " + message);

        SmsManager smsManager = SmsManager.getDefault();
        for (String contact : emergencyContacts) {
            try {
                ArrayList<String> parts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(contact, null, parts, null, null);
                Log.d(TAG, "SMS sent to: " + contact);
            } catch (Exception e) {
                Log.e(TAG, "Failed to send SMS to " + contact, e);
            }
        }
    }

    private void storeEmergencyEvent(String userId) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted for storing event");
            return;
        }

        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Store emergency event in Firebase
            DatabaseReference emergencyRef = firebaseDatabase.getReference()
                    .child("users")
                    .child(userId)
                    .child("emergencyEvents")
                    .push();

            Map<String, Object> emergencyEvent = new HashMap<>();
            emergencyEvent.put("timestamp", ServerValue.TIMESTAMP);

            if (location != null) {
                emergencyEvent.put("latitude", location.getLatitude());
                emergencyEvent.put("longitude", location.getLongitude());
            }

            emergencyRef.setValue(emergencyEvent)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Emergency event stored successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to store emergency event", e));

        } catch (Exception e) {
            Log.e(TAG, "Error storing emergency event", e);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "SOS Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Women Safeguard")
                .setContentText("SOS Service Running")
                .setSmallIcon(R.drawable.ic_warning) // Make sure to create this icon
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }
}