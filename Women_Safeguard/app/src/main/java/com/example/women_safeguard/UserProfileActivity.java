package com.example.women_safeguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.women_safeguard.database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    // Individual TextViews for each profile field
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView usernameTextView;
    private TextView addressTextView;
    private TextView contactTextView;
    private TextView emergency1TextView;
    private TextView emergency2TextView;
    private TextView emergency3TextView;
    private Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            // User not logged in, redirect to login
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        // Initialize UI components
        initializeViews();

        // Set up listeners
        setupListeners();

        // Fetch user data
        fetchUserProfile();
    }

    private void initializeViews() {
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        addressTextView = findViewById(R.id.addressTextView);
        contactTextView = findViewById(R.id.contactTextView);
        emergency1TextView = findViewById(R.id.emergency1TextView);
        emergency2TextView = findViewById(R.id.emergency2TextView);
        emergency3TextView = findViewById(R.id.emergency3TextView);
        editProfileButton = findViewById(R.id.editProfileButton);
    }

    private void setupListeners() {
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement edit profile functionality
                Toast.makeText(UserProfileActivity.this, "Edit Profile feature coming soon", Toast.LENGTH_SHORT).show();
                // Intent to edit profile activity can be added here
                // startActivity(new Intent(UserProfileActivity.this, EditProfileActivity.class));
            }
        });
    }

    private void fetchUserProfile() {
        // Show loading state
        showLoading(true);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    updateUI(user);
                } else {
                    Toast.makeText(UserProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }

                // Hide loading state
                showLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(UserProfileActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                // Hide loading state
                showLoading(false);
            }
        });
    }

    private void updateUI(User user) {
        // Set individual text views with user data
        nameTextView.setText(user.fullName);
        emailTextView.setText(user.email);
        usernameTextView.setText(user.username);
        addressTextView.setText(user.localAddress);
        contactTextView.setText(user.contactNumber);
        emergency1TextView.setText(user.emergency1);
        emergency2TextView.setText(user.emergency2);
        emergency3TextView.setText(user.emergency3);

        // Update title in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(user.fullName + "'s Profile");
        }
    }

    private void showLoading(boolean isLoading) {
        // Implement loading state here
        // For example, you could use a ProgressBar
        if (isLoading) {
            // Show progress
        } else {
            // Hide progress
        }
    }
}