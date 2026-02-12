package com.example.women_safeguard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.women_safeguard.database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SafetyCheck extends AppCompatActivity {
    private TextView timerText;
    private ProgressBar progressBar;
    private Button okButton, shareButton, callButton;
    private ImageView closeButton;  // Fix for Close Icon
    private CountDownTimer countDownTimer;
    private Vibrator vibrator;
    private LocationManager locationManager;

    private static final long TOTAL_TIME = 60000; // 60 seconds
    private static final long INTERVAL = 1000; // 1 second

    private String lastLocation = "Unknown Location";
    private String[] emergencyNumbers = new String[3];  // 🔹 Emergency contact numbers
    private final String directCallNumber = "9322043077";  // 🔥 Direct Call Number
    private static final String TAG = "SafetyCheck";

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_check1);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        timerText = findViewById(R.id.timerText);
        progressBar = findViewById(R.id.progressBar);
        okButton = findViewById(R.id.okButton);
        shareButton = findViewById(R.id.shareButton);
        callButton = findViewById(R.id.callButton);
        closeButton = findViewById(R.id.closeButton);  // Fix for Close Button
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        progressBar.setMax(60); // Max progress is 60 seconds
        requestPermissions();
        startCountdown();

        // Stop countdown when "I'm OK" button is pressed
        okButton.setOnClickListener(view -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            if (vibrator != null) {
                vibrator.cancel();
            }
            timerText.setText("✅ Safe");
            Toast.makeText(this, "Alert Canceled!", Toast.LENGTH_SHORT).show();
            finish(); // Close Activity
        });

        // Share emergency alert when "Start Sharing Now" is pressed
        shareButton.setOnClickListener(view -> {
            Log.d(TAG, "Share button clicked");
            getLocationAndSendAlert();
        });

        // Auto Call When Clicking "Call 112"
        callButton.setOnClickListener(view -> makeEmergencyCall());

        // Fix for Close Button (If available)
        if (closeButton != null) {
            closeButton.setOnClickListener(view -> {
                finish();  // Close Activity
            });
        }

        // Retrieve emergency contacts from Firebase
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    emergencyNumbers[0] = user.emergency1;
                    emergencyNumbers[1] = user.emergency2;
                    emergencyNumbers[2] = user.emergency3;
                    Log.d(TAG, "Emergency contacts retrieved from Firebase");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read user data", databaseError.toException());
            }
        });
    }

    // Request necessary permissions (Location, SMS, and Call)
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting location permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getLocation();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting SMS permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting CALL permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 3);
        }
    }

    // Get live location
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission granted, requesting location updates");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    lastLocation = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                    Log.d(TAG, "Location updated: " + lastLocation);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.d(TAG, "GPS provider disabled, trying network provider");
                    if (ActivityCompat.checkSelfPermission(SafetyCheck.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SafetyCheck.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Request the missing permissions
                        ActivityCompat.requestPermissions(SafetyCheck.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
                }
            });
        } else {
            Log.d(TAG, "Location permission not granted");
        }
    }

    // Start countdown with vibration
    private void startCountdown() {
        countDownTimer = new CountDownTimer(TOTAL_TIME, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                timerText.setText(String.valueOf(secondsLeft));
                progressBar.setProgress(secondsLeft);

                if (vibrator != null) {
                    if (secondsLeft <= 10) { // Vibrate every second in last 10 sec
                        vibrator.vibrate(500);
                    } else if (secondsLeft % 5 == 0) { // Vibrate every 5 sec otherwise
                        vibrator.vibrate(300);
                    }
                }
            }

            @Override
            public void onFinish() {
                timerText.setText("⚠️ Time's up!");
                progressBar.setProgress(0);
                if (vibrator != null) {
                    vibrator.vibrate(1000); // Final long vibration
                }
                getLocationAndSendAlert(); // Auto-send alert when time is up
            }
        }.start();
    }

    // Get location and send emergency SMS with location
    private void getLocationAndSendAlert() {
        // Get the location before sending the alert
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission granted, requesting single update");
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    lastLocation = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                    Log.d(TAG, "Single location update: " + lastLocation);
                    sendEmergencyAlert();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.d(TAG, "GPS provider disabled, trying network provider");
                    if (ActivityCompat.checkSelfPermission(SafetyCheck.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SafetyCheck.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Request the missing permissions
                        ActivityCompat.requestPermissions(SafetyCheck.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        return;
                    }
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
                }
            }, Looper.getMainLooper());
        } else {
            Log.d(TAG, "Location permission not granted");
            sendEmergencyAlert(); // Send alert with the last known location
        }
    }

    // Send emergency SMS with location
    private void sendEmergencyAlert() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                String message = "Emergency! I need help. My location is: " + lastLocation;
                Log.d(TAG, "Sending message: " + message);

                if (lastLocation.equals("Unknown Location")) {
                    message = "🚨 Emergency! I need help. My location is currently unknown.";
                    Log.d(TAG, "Last location is unknown, sending default message");
                }

                // Send SMS to all emergency numbers
                for (String number : emergencyNumbers) {
                    if (number != null && !number.isEmpty()) {
                        Log.d(TAG, "Sending SMS to " + number);
                        sendSms(smsManager, number, message);
                    }
                }

                Toast.makeText(this, "🚀 Emergency Alert Sent!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Emergency alert sent: " + message);
            } catch (Exception e) {
                Toast.makeText(this, "⚠️ Failed to send SMS!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to send SMS", e);
            }
        } else {
            Toast.makeText(this, "⚠️ SMS permission not granted!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "SMS permission not granted");
        }
    }

    // Helper method to send SMS
    private void sendSms(SmsManager smsManager, String phoneNumber, String message) {
        try {
            ArrayList<String> messageParts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, null, null);
            Log.d(TAG, "SMS sent to " + phoneNumber + " with message: " + message);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS to " + phoneNumber + " with message: " + message, e);
        }
    }

    // Auto Call Function
    private void makeEmergencyCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + directCallNumber));
                startActivity(callIntent);
            } catch (SecurityException e) {
                Toast.makeText(this, "⚠️ CALL permission not granted!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Call permission not granted", e);
            }
        } else {
            Toast.makeText(this, "⚠️ CALL permission not granted!", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
        if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendEmergencyAlert();
        }
        if (requestCode == 3 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeEmergencyCall();
        }
    }
}