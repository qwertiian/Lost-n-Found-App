package com.example.lostandfound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity implements PendingClaimsAdapter.OnClaimActionListener {

    private TextView tvWelcome;
    private RecyclerView rvPendingClaims;
    private Button btnViewAllItems, btnLogout;
    private ImageButton btnBack;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        rvPendingClaims = findViewById(R.id.rvPendingClaims);
        btnViewAllItems = findViewById(R.id.btnViewAllItems);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

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
        });

        btnLogout.setOnClickListener(v -> logout());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupPendingClaimsRecycler() {
        try {
            List<Item> pendingClaims = databaseHelper.getItemsByStatus("claimed");
            if (pendingClaims != null && !pendingClaims.isEmpty()) {
                PendingClaimsAdapter adapter = new PendingClaimsAdapter(pendingClaims, this);
                rvPendingClaims.setLayoutManager(new LinearLayoutManager(this));
                rvPendingClaims.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No pending claims found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading claims", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void logout() {
        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onApproveClaim(Item item) {
        try {
            int result = databaseHelper.updateItemStatus(item.getId(), "returned", item.getClaimerId(), item.getClaimProof());
            if (result > 0) {
                Toast.makeText(this, "Claim approved", Toast.LENGTH_SHORT).show();
                setupPendingClaimsRecycler();
            } else {
                Toast.makeText(this, "Failed to approve claim", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error approving claim", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRejectClaim(Item item) {
        try {
            int result = databaseHelper.updateItemStatus(item.getId(), "lost", -1, null);
            if (result > 0) {
                Toast.makeText(this, "Claim rejected", Toast.LENGTH_SHORT).show();
                setupPendingClaimsRecycler();
            } else {
                Toast.makeText(this, "Failed to reject claim", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error rejecting claim", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupPendingClaimsRecycler();
    }
}