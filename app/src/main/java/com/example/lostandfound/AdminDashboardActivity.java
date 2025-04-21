package com.example.lostandfound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity implements PendingClaimsAdapter.OnClaimActionListener {

    private TextView tvWelcome;
    private RecyclerView rvPendingClaims;
    private Button btnViewAllItems, btnLogout;
    private Toolbar toolbar;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private PendingClaimsAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Admin Dashboard");
        }

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        rvPendingClaims = findViewById(R.id.rvPendingClaims);
        btnViewAllItems = findViewById(R.id.btnViewAllItems);
        btnLogout = findViewById(R.id.btnLogout);

        Button btnPendingClaims = findViewById(R.id.btnPendingClaims);
        btnPendingClaims.setOnClickListener(v -> {
            // Refresh the claims list when the button is clicked
            refreshClaimsList();
        });

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LostAndFoundPrefs", MODE_PRIVATE);

        // Verify admin status
        if (!sharedPreferences.getBoolean("isAdmin", false)) {
            Toast.makeText(this, "Admin access required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userName = sharedPreferences.getString("userName", "Admin");
        tvWelcome.setText("Welcome, " + userName + " (Admin)");

        setupPendingClaimsRecycler();

        btnViewAllItems.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ItemsListActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void setupPendingClaimsRecycler() {
        try {
            List<Item> pendingClaims = databaseHelper.getPendingClaims();

            // Always set up the adapter, even if the list is empty
            adapter = new PendingClaimsAdapter(pendingClaims != null ? pendingClaims : new ArrayList<>(), this);
            rvPendingClaims.setLayoutManager(new LinearLayoutManager(this));
            rvPendingClaims.setAdapter(adapter);

            // Show a message if there are no claims, but don't exit the method
            if (pendingClaims == null || pendingClaims.isEmpty()) {
                Toast.makeText(this, "No pending claims found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading claims: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void logout() {
        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    // Also update the parameter types in your onApproveClaim and onRejectClaim methods:
    @Override
    public void onApproveClaim(Item item) {
        try {
            boolean result = databaseHelper.approveClaim(
                    item.getId(),
                    item.getClaimerId(),
                    item.getClaimProof()
            );

            if (result) {
                Toast.makeText(this, "Claim approved successfully", Toast.LENGTH_SHORT).show();
                refreshClaimsList();
            } else {
                Toast.makeText(this, "Failed to approve claim", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error approving claim: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRejectClaim(Item item) {
        try {
            boolean result = databaseHelper.rejectClaim(item.getId());
            if (result) {
                Toast.makeText(this, "Claim rejected successfully", Toast.LENGTH_SHORT).show();
                refreshClaimsList();
            } else {
                Toast.makeText(this, "Failed to reject claim", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error rejecting claim: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void refreshClaimsList() {
        List<Item> updatedClaims = databaseHelper.getPendingClaims();
        if (adapter != null) {
            adapter.updateData(updatedClaims);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshClaimsList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
}