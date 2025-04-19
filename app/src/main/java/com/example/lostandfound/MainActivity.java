package com.example.lostandfound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000; // 1 second delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("LostAndFoundPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getLong("userId", -1) != -1;
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        new Handler().postDelayed(() -> {
            Intent intent;
            if (isLoggedIn) {
                if (isAdmin) {
                    intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, HomeActivity.class);
                }
            } else {
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish(); // Close this activity
        }, SPLASH_DELAY);
    }
}