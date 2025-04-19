package com.example.lostandfound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnReport, btnViewItems;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnReport = findViewById(R.id.btnReport);
        btnViewItems = findViewById(R.id.btnViewItems);

        sharedPreferences = getSharedPreferences("LostAndFoundPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "User");
        tvWelcome.setText("Welcome, " + userName + "!");

        btnReport.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ReportActivity.class));
        });

        btnViewItems.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ItemsListActivity.class));
        });

        // Add these to your existing HomeActivity
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnLogout = findViewById(R.id.btnLogout);

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