package com.example.lostandfound;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private long userId;
    private Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        Button btnUpdate = findViewById(R.id.btnUpdate);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LostAndFoundPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);

        // Load current user data
        loadProfileData();

        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProfileData() {
        if (userId != -1) {
            User user = databaseHelper.getUser(userId);
            if (user != null) {
                etName.setText(user.getName());
                etEmail.setText(user.getEmail());
                etPhone.setText(user.getPhone());
            }
        }
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName(name);
        updatedUser.setEmail(email);
        updatedUser.setPhone(phone);
        updatedUser.setAdmin(sharedPreferences.getBoolean("isAdmin", false));

        boolean success = databaseHelper.updateUser(updatedUser);

        if (success) {
            // Update shared preferences
            sharedPreferences.edit()
                    .putString("userName", name)
                    .apply();

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
}