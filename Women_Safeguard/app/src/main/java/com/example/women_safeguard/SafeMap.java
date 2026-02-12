package com.example.women_safeguard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.women_safeguard.database.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SafeMap extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 200;
    private static final int LOCATION_PERMISSION_REQUEST = 300;
    private static final String TAG = "SafeMap";

    private EditText descriptionInput;
    private ImageView imagePreview;
    private TextView locationText;
    private Button captureImageBtn, uploadImageBtn, submitFeedbackBtn;

    private Uri selectedImageUri;
    private Bitmap capturedImage;
    private LocationManager locationManager;
    private String currentLocation = "Fetching location...";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_map);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("reports");

        // Initialize UI components
        descriptionInput = findViewById(R.id.descriptionInput);
        imagePreview = findViewById(R.id.imagePreview);
        locationText = findViewById(R.id.locationText);
        captureImageBtn = findViewById(R.id.captureImageBtn);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        submitFeedbackBtn = findViewById(R.id.submitFeedbackBtn);

        // Request location permissions
        requestLocationPermission();

        // Capture Image Button Click
        captureImageBtn.setOnClickListener(view -> openCamera());

        // Upload Image Button Click
        uploadImageBtn.setOnClickListener(view -> openGallery());

        // Submit Feedback Button Click
        submitFeedbackBtn.setOnClickListener(view -> submitReport());
    }

    // Open Camera to Capture Image
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    // Open Gallery to Select Image
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    // Handle Image Selection Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                capturedImage = (Bitmap) data.getExtras().get("data");
                imagePreview.setImageBitmap(capturedImage);
                imagePreview.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                selectedImageUri = data.getData();
                imagePreview.setImageURI(selectedImageUri);
                imagePreview.setVisibility(View.VISIBLE);
            }
        }
    }

    // Request Location Permission
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            getCurrentLocation();
        }
    }

    // Fetch Current Location using Network and GPS Providers
    private void getCurrentLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                // First use Network provider for a quick location
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        currentLocation = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                        locationText.setText(currentLocation);
                    }

                    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
                    @Override public void onProviderEnabled(String provider) {}
                    @Override public void onProviderDisabled(String provider) {}
                });

                // Then use GPS provider for a more accurate location
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        currentLocation = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                        locationText.setText(currentLocation);
                    }

                    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
                    @Override public void onProviderEnabled(String provider) {}
                    @Override public void onProviderDisabled(String provider) {}
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // Handle Permission Request Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Submit Report
    private void submitReport() {
        String description = descriptionInput.getText().toString().trim();

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (capturedImage == null && selectedImageUri == null) {
            Toast.makeText(this, "Please capture or upload an image!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentLocation.equals("Fetching location...")) {
            Toast.makeText(this, "Please wait for location to be fetched!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save image locally and upload coordinates to Firebase
        if (capturedImage != null) {
            saveCapturedImage(description);
        } else if (selectedImageUri != null) {
            saveSelectedImage(description);
        }
    }

    // Save Captured Image Locally
    private void saveCapturedImage(String description) {
        try {
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(imageFile);
            capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            String imagePath = imageFile.getAbsolutePath();
            saveReportToDatabase(description, imagePath);
        } catch (IOException e) {
            Toast.makeText(SafeMap.this, "Failed to save image", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Saving image failed", e);
        }
    }

    // Save Selected Image Locally
    private void saveSelectedImage(String description) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            String imagePath = imageFile.getAbsolutePath();
            saveReportToDatabase(description, imagePath);
        } catch (IOException e) {
            Toast.makeText(SafeMap.this, "Failed to save image", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Saving image failed", e);
        }
    }

    // Save Report to Firebase Realtime Database
    // Save Report to Firebase Realtime Database
    private void saveReportToDatabase(String description, String imagePath) {
        String userId = mAuth.getCurrentUser().getUid();
        String reportId = databaseReference.push().getKey();

        Report report = new Report(userId, description, currentLocation, imagePath);
        report.setStatus("pending"); // Set initial status as pending

        if (reportId != null) {
            databaseReference.child(reportId).setValue(report).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SafeMap.this, "Report submitted successfully! Awaiting admin approval.", Toast.LENGTH_LONG).show();
                    clearForm();
                } else {
                    Toast.makeText(SafeMap.this, "Failed to submit report", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Clear Form
    private void clearForm() {
        descriptionInput.setText("");
        imagePreview.setImageBitmap(null);
        imagePreview.setVisibility(View.GONE);
        currentLocation = "Fetching location...";
        locationText.setText(currentLocation);
        capturedImage = null;
        selectedImageUri = null;
    }
}