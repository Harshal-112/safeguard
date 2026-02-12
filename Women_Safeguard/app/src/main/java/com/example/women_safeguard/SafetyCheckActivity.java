package com.example.women_safeguard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SafetyCheckActivity extends AppCompatActivity {

    private EditText hoursInput, minutesInput, secondsInput;
    private Button submitButton, stopButton;
    private Handler handler = new Handler();
    private Runnable safetyCheckRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safetycheck);

        // Initialize UI elements
        hoursInput = findViewById(R.id.hours_input);
        minutesInput = findViewById(R.id.minutes_input);
        secondsInput = findViewById(R.id.seconds_input);
        submitButton = findViewById(R.id.submit_button);
        stopButton = findViewById(R.id.close_button);

        // Handle submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoursText = hoursInput.getText().toString().trim();
                String minutesText = minutesInput.getText().toString().trim();
                String secondsText = secondsInput.getText().toString().trim();

                int hours = hoursText.isEmpty() ? 0 : Integer.parseInt(hoursText);
                int minutes = minutesText.isEmpty() ? 0 : Integer.parseInt(minutesText);
                int seconds = secondsText.isEmpty() ? 0 : Integer.parseInt(secondsText);

                if (hours == 0 && minutes == 0 && seconds == 0) {
                    Toast.makeText(SafetyCheckActivity.this, "Please set a valid time", Toast.LENGTH_SHORT).show();
                } else {
                    int totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
                    long delayMillis = totalSeconds * 1000;
                    Toast.makeText(SafetyCheckActivity.this, "Safety Check Set for " + totalSeconds + " seconds", Toast.LENGTH_SHORT).show();

                    // Schedule SafetyCheck to be called repeatedly after the specified time
                    if (safetyCheckRunnable != null) {
                        handler.removeCallbacks(safetyCheckRunnable);
                    }
                    safetyCheckRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SafetyCheckActivity.this, SafetyCheck.class);
                            startActivity(intent);

                            // Re-schedule the runnable to run again after the specified delay
                            handler.postDelayed(this, delayMillis);
                        }
                    };
                    handler.postDelayed(safetyCheckRunnable, delayMillis);
                }
            }
        });

        // Handle stop button click
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (safetyCheckRunnable != null) {
                    handler.removeCallbacks(safetyCheckRunnable);
                    Toast.makeText(SafetyCheckActivity.this, "Safety Check stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (safetyCheckRunnable != null) {
            handler.removeCallbacks(safetyCheckRunnable);
        }
    }
}