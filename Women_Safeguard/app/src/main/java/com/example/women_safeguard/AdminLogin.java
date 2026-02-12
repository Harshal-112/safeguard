package com.example.women_safeguard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLogin extends AppCompatActivity {

    // Default admin credentials
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    // Shared preferences file and keys
    private static final String ADMIN_PREFS = "admin_preferences";
    private static final String KEY_IS_LOGGED_IN = "is_admin_logged_in";

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Check if already logged in
        if (isAdminLoggedIn()) {
            proceedToAdminPanel();
            return;
        }

        // Initialize UI components
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);

        buttonLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        // Reset errors
        editTextUsername.setError(null);
        editTextPassword.setError(null);

        // Get values
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate inputs
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            focusView = editTextPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Username is required");
            focusView = editTextUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; focus the first form field with an error
            focusView.requestFocus();
        } else {
            // Show a progress spinner and attempt authentication
            progressBar.setVisibility(View.VISIBLE);

            // Simulate network delay (can be removed in production)
            progressBar.postDelayed(() -> {
                progressBar.setVisibility(View.GONE);

                // Check against default credentials
                if (username.equals(DEFAULT_ADMIN_USERNAME) && password.equals(DEFAULT_ADMIN_PASSWORD)) {
                    // Authentication success
                    setAdminLoggedIn(true);
                    proceedToAdminPanel();
                } else {
                    // Authentication failed
                    Toast.makeText(AdminLogin.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }, 1000);
        }
    }

    private boolean isAdminLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(ADMIN_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void setAdminLoggedIn(boolean loggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences(ADMIN_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, loggedIn);
        editor.apply();
    }

    private void proceedToAdminPanel() {
        Intent intent = new Intent(AdminLogin.this, AdminPanel.class);
        startActivity(intent);
        finish(); // Prevent going back to login screen with back button
    }
}