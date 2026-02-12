package com.example.women_safeguard

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity() {
    private lateinit var dataClient: DataClient
    private val TAG = "WatchSOS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the Wearable DataClient
        dataClient = Wearable.getDataClient(this)

        // Set up SOS button click listener
        findViewById<Button>(R.id.sosButton).setOnClickListener {
            sendSosSignal()
        }
    }

    private fun sendSosSignal() {
        Log.d(TAG, "Attempting to send SOS signal")

        try {
            // Create data item request
            val request = PutDataMapRequest.create("/sos_trigger").apply {
                dataMap.putLong("timestamp", System.currentTimeMillis())
                dataMap.putBoolean("sos_activated", true)
            }.asPutDataRequest()

            // Make it urgent (high priority)
            request.setUrgent()

            // Send the data with retry mechanism
            sendDataWithRetry(request, 3)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating SOS request", e)
            showError("Failed to create SOS request: ${e.message}")
        }
    }

    private fun sendDataWithRetry(request: PutDataRequest, retriesLeft: Int) {
        dataClient.putDataItem(request)
            .addOnSuccessListener {
                Log.d(TAG, "SOS data sent successfully")
                vibrate()
                showConfirmation("SOS Sent")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to send SOS data: ${e.message}")
                if (retriesLeft > 0) {
                    Log.d(TAG, "Retrying... ($retriesLeft attempts left)")
                    sendDataWithRetry(request, retriesLeft - 1)
                } else {
                    showError("Failed to send SOS after multiple attempts")
                }
            }
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(1000)
        }
    }

    private fun showConfirmation(message: String) {
        // Display confirmation UI
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        // Display error UI
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}