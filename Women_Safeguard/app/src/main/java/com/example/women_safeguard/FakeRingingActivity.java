package com.example.women_safeguard;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.women_safeguard.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class FakeRingingActivity extends AppCompatActivity {

    private String networkCarrier;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_ringing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.tonec); // Replace 'tonec' with your actual file name
            if (mediaPlayer != null) {
                mediaPlayer.start(); // Start playing the ringtone
            } else {
                Log.e("RingtoneActivity", "MediaPlayer initialization failed.");
            }
        } catch (Exception e) {
            Log.e("RingtoneActivity", "Error playing ringtone: " + e.getMessage());
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView fakeName = findViewById(R.id.chosenfakename);
        TextView fakeNumber = findViewById(R.id.chosenfakenumber);

        final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        networkCarrier = tm.getNetworkOperatorName();

        TextView titleBar = findViewById(R.id.textView1);
        if (networkCarrier != null) {
            titleBar.setText("Incoming call - " + networkCarrier);
        } else {
            titleBar.setText("Incoming call");
        }

        String callNumber = getContactNumber();
        String callName = getContactName();

        fakeName.setText(callName);
        fakeNumber.setText(callNumber);

        Button answerCall = findViewById(R.id.answercall);
        Button rejectCall = findViewById(R.id.rejectcall);

        answerCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }
        });

        rejectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                Intent in1 = new Intent(getApplication(), MainActivity.class);
                startActivity(in1);
            }
        });
    }

    private String getContactNumber() {
        String contact = null;
        Intent myIntent = getIntent();
        Bundle mIntent = myIntent.getExtras();
        if (mIntent != null) {
            contact = mIntent.getString("myfakenumber");
        }
        return contact;
    }

    private String getContactName() {
        String contactName = null;
        Intent myIntent = getIntent();
        Bundle mIntent = myIntent.getExtras();
        if (mIntent != null) {
            contactName = mIntent.getString("myfakename");
        }
        return contactName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}