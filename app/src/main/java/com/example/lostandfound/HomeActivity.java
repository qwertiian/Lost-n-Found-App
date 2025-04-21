package com.example.lostandfound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnReport, btnViewItems;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        btnReport = findViewById(R.id.btnReport);
        btnViewItems = findViewById(R.id.btnViewItems);
        Button btnProfile;
        btnProfile = findViewById(R.id.btnProfile);
        Button btnLogout;
        btnLogout = findViewById(R.id.btnLogout);

        sharedPreferences = getSharedPreferences("LostAndFoundPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "User");
        tvWelcome.setText("Welcome, " + userName + "!");

        // Set click listeners with null checks
        if (btnReport != null) {
            btnReport.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, ReportActivity.class));
            });
        }

        if (btnViewItems != null) {
            btnViewItems.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, ItemsListActivity.class));
            });
        }

        btnReport.setOnClickListener(v -> {
            try {
                startActivity(new Intent(HomeActivity.this, ReportActivity.class));
            } catch (Exception e) {
                Toast.makeText(HomeActivity.this, "Error opening report form", Toast.LENGTH_SHORT).show();
                Log.e("HomeActivity", "Error starting ReportActivity", e);
            }
        });

        btnViewItems.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ItemsListActivity.class));
        });

        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("LostAndFoundPrefs", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }
}