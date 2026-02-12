package com.example.women_safeguard;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class BatterySettingsActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Slider batterySlider;
    private TextView percentageTextView;
    private SwitchMaterial enableAlertSwitch;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_settings);

        // Check and request permissions
        checkAndRequestPermissions();

        // Initialize views
        batterySlider = findViewById(R.id.batterySlider);
        percentageTextView = findViewById(R.id.percentageTextView);
        enableAlertSwitch = findViewById(R.id.enableAlertSwitch);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("BatterySettings", MODE_PRIVATE);

        // Load saved settings
        int savedThreshold = sharedPreferences.getInt("batteryThreshold", 15);
        boolean alertsEnabled = sharedPreferences.getBoolean("alertsEnabled", false);

        batterySlider.setValue(savedThreshold);
        enableAlertSwitch.setChecked(alertsEnabled);

        // Set up Slider listener
        batterySlider.addOnChangeListener((slider, value, fromUser) ->
                percentageTextView.setText(String.format("%.0f%%", value))
        );

        // Save button click listener
        findViewById(R.id.saveButton).setOnClickListener(v -> saveSettings());

        // Stop Messages button click listener
        findViewById(R.id.stopMessagesButton).setOnClickListener(v -> stopMessages());
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("batteryThreshold", (int) batterySlider.getValue());
        editor.putBoolean("alertsEnabled", enableAlertSwitch.isChecked());
        editor.apply();

        // Start or stop the service based on switch state
        Intent serviceIntent = new Intent(this, BatteryMonitorService.class);
        if (enableAlertSwitch.isChecked()) {
            startService(serviceIntent);
        } else {
            stopService(serviceIntent);
        }

        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void stopMessages() {
        // Reset the message sent flag in the service
        Intent intent = new Intent(this, BatteryMonitorService.class);
        intent.setAction("RESET_MESSAGE_FLAG");
        startService(intent);

        Toast.makeText(this, "Emergency messages stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission " + permissions[i] + " denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}