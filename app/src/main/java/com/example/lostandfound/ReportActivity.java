package com.example.lostandfound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    // UI Components
    private TextInputEditText etItemName, etDate, etLocation, etDescription;
    private Button btnAddImage, btnTakePhoto, btnSubmit;
    private ImageView ivItem;
    private ProgressBar progressBar;
    private TextView tvProgress;
    private ImageButton btnBack;

    // Data
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private String imagePath = "";
    private int progress = 0;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize views
        initializeViews();

        // Set current date as default
        setDefaultDate();

        // Setup listeners
        setupListeners();

        if (btnBack == null) {
            btnBack = findViewById(R.id.btnBack);
        }
    }

    @SuppressLint("WrongViewCast")
    private void initializeViews() {
        try {
            etItemName = findViewById(R.id.etItemName);
            etDate = findViewById(R.id.etDate);
            etLocation = findViewById(R.id.etLocation);
            etDescription = findViewById(R.id.etDescription);
            btnAddImage = findViewById(R.id.btnAddImage);
            btnTakePhoto = findViewById(R.id.btnTakePhoto);
            btnSubmit = findViewById(R.id.btnSubmit);
            ivItem = findViewById(R.id.ivItem);
            progressBar = findViewById(R.id.progressBar);
            tvProgress = findViewById(R.id.tvProgress);
            btnBack = findViewById(R.id.btnBack);

            // Initialize database and shared preferences
            databaseHelper = new DatabaseHelper(this);
            sharedPreferences = getSharedPreferences("LostAndFoundPrefs", MODE_PRIVATE);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setDefaultDate() {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        etDate.setText(currentDate);
    }

    private void setupListeners() {
        // Text watchers for progress tracking
        TextWatcher progressWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateProgress();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        etItemName.addTextChangedListener(progressWatcher);
        etDate.addTextChangedListener(progressWatcher);
        etLocation.addTextChangedListener(progressWatcher);
        etDescription.addTextChangedListener(progressWatcher);

        // Back button listener
        btnBack.setOnClickListener(v -> {
            if (hasUnsavedChanges()) {
                showExitConfirmationDialog();
            } else {
                finish();
            }
        });

        btnAddImage.setOnClickListener(v -> openGallery());
        btnTakePhoto.setOnClickListener(v -> openCamera());
        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private boolean hasUnsavedChanges() {
        return !etItemName.getText().toString().isEmpty() ||
                !etLocation.getText().toString().isEmpty() ||
                !etDescription.getText().toString().isEmpty() ||
                !imagePath.isEmpty();
    }

    private void showExitConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("You have unsaved changes. Are you sure you want to exit?")
                .setPositiveButton("Discard", (dialog, which) -> finish())
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (hasUnsavedChanges()) {
            showExitConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void updateProgress() {
        int totalFields = 4; // name, date, location, description
        int filledFields = 0;

        // Safely check each field
        if (etItemName.getText() != null && !etItemName.getText().toString().trim().isEmpty()) filledFields++;
        if (etDate.getText() != null && !etDate.getText().toString().trim().isEmpty()) filledFields++;
        if (etLocation.getText() != null && !etLocation.getText().toString().trim().isEmpty()) filledFields++;
        if (etDescription.getText() != null && !etDescription.getText().toString().trim().isEmpty()) filledFields++;

        progress = (filledFields * 100) / totalFields;
        progressBar.setProgress(progress);
        tvProgress.setText("Progress: " + progress + "%");

        // Enable submit only when all required fields are filled
        btnSubmit.setEnabled(filledFields == totalFields);
    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            Toast.makeText(this, "Gallery access error", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() {
        Toast.makeText(this, "Camera functionality coming soon", Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                ivItem.setImageURI(imageUri);
                imagePath = imageUri.toString();
                updateProgress();
            }
        }
    }

    // Update the submitReport method with better error handling
    private void submitReport() {
        try {
            if (etItemName == null || etDate == null || etLocation == null || etDescription == null) {
                Toast.makeText(this, "Form fields not properly initialized", Toast.LENGTH_SHORT).show();
                return;
            }

            String itemName = etItemName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            long reporterId = sharedPreferences.getLong("userId", -1);
            if (reporterId == -1) {
                Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            // Create and save item
            Item item = new Item();
            item.setName(itemName);
            item.setDate(date);
            item.setLocation(location);
            item.setDescription(description);
            item.setImage(imagePath);
            item.setStatus("lost");
            item.setReporterId(reporterId);

            long id = databaseHelper.addItem(item);
            if (id != -1) {
                Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to submit report", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error submitting report: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}