package com.example.women_safeguard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.women_safeguard.database.Report;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdminPanel extends AppCompatActivity {

    private static final String TAG = "AdminPanel";
    private static final String ADMIN_PREFS = "admin_preferences";
    private static final String KEY_IS_LOGGED_IN = "is_admin_logged_in";

    private RecyclerView recyclerView;
    private ReportsAdapter adapter;
    private List<Report> pendingReports;
    private List<String> reportIds;
    private DatabaseReference databaseReference;
    private Button logout_button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Panel");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewReports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pendingReports = new ArrayList<>();
        reportIds = new ArrayList<>();
        adapter = new ReportsAdapter(pendingReports, reportIds);
        recyclerView.setAdapter(adapter);
        logout_button = findViewById(R.id.logout_button);
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("reports");

        // Fetch pending reports
        fetchPendingReports();

        // In AdminPanel.java, modify the logout button click listener:
        logout_button.setOnClickListener(v -> {
            // Clear admin login state
            SharedPreferences sharedPreferences = getSharedPreferences(ADMIN_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, false);
            editor.apply();

            // Navigate to login screen
            Intent intent = new Intent(AdminPanel.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Add this to close the current activity
        });
    }

    private void fetchPendingReports() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pendingReports.clear();
                reportIds.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);
                    String reportId = snapshot.getKey();

                    if (report != null) {
                        // Filter reports that are pending or null status
                        if (report.getStatus() == null || report.getStatus().equals("pending")) {
                            pendingReports.add(report);
                            reportIds.add(reportId);
                        }
                    }
                }

                adapter.notifyDataSetChanged();

                if (pendingReports.isEmpty()) {
                    Toast.makeText(AdminPanel.this, "No pending reports to review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching reports: " + databaseError.getMessage());
                Toast.makeText(AdminPanel.this, "Failed to load reports", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adapter for the RecyclerView
    private class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {

        private List<Report> reports;
        private List<String> reportIds;

        ReportsAdapter(List<Report> reports, List<String> reportIds) {
            this.reports = reports;
            this.reportIds = reportIds;
        }

        @NonNull
        @Override
        public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
            return new ReportViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
            Report report = reports.get(position);
            String reportId = reportIds.get(position);

            // Set report details
            holder.textDescription.setText(report.getDescription());
            holder.textLocation.setText(report.getLocation());

            // Load image if available
            if (report.getImagePath() != null && !report.getImagePath().isEmpty()) {
                File imageFile = new File(report.getImagePath());
                if (imageFile.exists()) {
                    Glide.with(AdminPanel.this)
                            .load(imageFile)
                            .centerCrop()
                            .into(holder.imageReport);
                } else {
                    holder.imageReport.setImageResource(R.drawable.ic_email);
                }
            } else {
                holder.imageReport.setImageResource(R.drawable.ic_email);
            }

            // Set button click listeners
            holder.btnApprove.setOnClickListener(v -> updateReportStatus(reportId, "approved"));
            holder.btnReject.setOnClickListener(v -> updateReportStatus(reportId, "rejected"));
        }

        @Override
        public int getItemCount() {
            return reports.size();
        }

        // ViewHolder class
        class ReportViewHolder extends RecyclerView.ViewHolder {
            TextView textDescription, textLocation;
            ImageView imageReport;
            Button btnApprove, btnReject;

            ReportViewHolder(@NonNull View itemView) {
                super(itemView);
                textDescription = itemView.findViewById(R.id.textDescription);
                textLocation = itemView.findViewById(R.id.textLocation);
                imageReport = itemView.findViewById(R.id.imageReport);
                btnApprove = itemView.findViewById(R.id.btnApprove);
                btnReject = itemView.findViewById(R.id.btnReject);
            }
        }
    }

    // Update report status in Firebase
    private void updateReportStatus(String reportId, String status) {
        databaseReference.child(reportId).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    String message = status.equals("approved") ? "Report approved" : "Report rejected";
                    Toast.makeText(AdminPanel.this, message, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminPanel.this, "Failed to update report status", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating report status", e);
                });
    }
}