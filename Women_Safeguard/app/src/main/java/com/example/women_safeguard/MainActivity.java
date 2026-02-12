package com.example.women_safeguard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.women_safeguard.database.User;
import com.example.women_safeguard.Dashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText fullName, username, email, password, localAddress, contactNumber, emergency1, emergency2, emergency3;
    private Button registerButton;
    private TextView loginRedirect;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize views
        fullName = findViewById(R.id.first_name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        localAddress = findViewById(R.id.local_address);
        contactNumber = findViewById(R.id.contact_number);
        emergency1 = findViewById(R.id.emergency_contact_1);
        emergency2 = findViewById(R.id.emergency_contact_2);
        emergency3 = findViewById(R.id.emergency_contact_3);
        registerButton = findViewById(R.id.register_button);
        loginRedirect = findViewById(R.id.login_link);

        // Handle Register button click
        registerButton.setOnClickListener(v -> {
            String fullNameText = fullName.getText().toString().trim();
            String usernameText = username.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String address = localAddress.getText().toString().trim();
            String userContact = contactNumber.getText().toString().trim();
            String em1 = emergency1.getText().toString().trim();
            String em2 = emergency2.getText().toString().trim();
            String em3 = emergency3.getText().toString().trim();

            // Validate empty fields
            if (fullNameText.isEmpty() || usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || address.isEmpty() ||
                    userContact.isEmpty() || em1.isEmpty() || em2.isEmpty() || em3.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate email format
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(MainActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate contact numbers (must be exactly 10 digits)
            if (!isValidPhoneNumber(userContact) || !isValidPhoneNumber(em1) || !isValidPhoneNumber(em2) || !isValidPhoneNumber(em3)) {
                Toast.makeText(MainActivity.this, "Contact numbers must be 10 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            // Register user with Firebase Auth
            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Save user data to Firebase Realtime Database
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                User newUser = new User(fullNameText, usernameText, emailText, passwordText, address, userContact, em1, em2, em3);
                                databaseReference.child(userId).setValue(newUser)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                // Show registration success popup
                                                new AlertDialog.Builder(MainActivity.this)
                                                        .setTitle("Registration Successful")
                                                        .setMessage("You have been successfully registered!")
                                                        .setPositiveButton("OK", (dialog, which) -> {
                                                            // Redirect to Home Page
                                                            Intent intent = new Intent(MainActivity.this, Dashboard.class);
                                                            startActivity(intent);
                                                            finish();
                                                        })
                                                        .show();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Handle login redirection
        loginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Function to validate a 10-digit phone number
    private boolean isValidPhoneNumber(String number) {
        return number.matches("\\d{10}");  // Ensures exactly 10 digits
    }
}