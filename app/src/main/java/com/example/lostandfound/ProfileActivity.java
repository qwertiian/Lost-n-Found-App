package com.example.lostandfound;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnBack = findViewById(R.id.btnBack);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LostAndFoundPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);

        // Load current user data
        loadProfileData();

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnBack.setOnClickListener(v -> finish());
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

        // In a real app, you would have an updateUser method in DatabaseHelper
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
}