package com.example.lostandfound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ItemDetailActivity extends AppCompatActivity {

    private ImageView ivItemImage;
    private TextView tvItemName, tvItemStatus, tvReportedBy, tvDate, tvLocation, tvDescription;
    private Button btnClaim, btnContactReporter;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private long itemId;
    private Item currentItem;
    private Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Item Details");
        }

        // Initialize views
        ivItemImage = findViewById(R.id.ivItemImage);
        tvItemName = findViewById(R.id.tvItemName);
        tvItemStatus = findViewById(R.id.tvItemStatus);
        tvReportedBy = findViewById(R.id.tvReportedBy);
        tvDate = findViewById(R.id.tvDate);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvDescription);
        btnClaim = findViewById(R.id.btnClaim);
        btnContactReporter = findViewById(R.id.btnContactReporter);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LostAndFoundPrefs", MODE_PRIVATE);

        itemId = getIntent().getLongExtra("itemId", -1);
        if (itemId == -1) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadItemDetails();

        btnClaim.setOnClickListener(v -> claimItem());

        btnContactReporter.setOnClickListener(v -> {
            if (currentItem != null) {
                User reporter = databaseHelper.getUser(currentItem.getReporterId());
                if (reporter != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + reporter.getPhone()));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void loadItemDetails() {
        currentItem = databaseHelper.getItem(itemId);
        if (currentItem == null) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        User reporter = databaseHelper.getUser(currentItem.getReporterId());

        // Set item details
        tvItemName.setText(currentItem.getName());
        tvItemStatus.setText("Status: " + currentItem.getStatus());
        tvReportedBy.setText("Reported by: " + (reporter != null ? reporter.getName() : "Unknown"));
        tvDate.setText("Date: " + currentItem.getDate());
        tvLocation.setText("Location: " + currentItem.getLocation());
        tvDescription.setText(currentItem.getDescription());

        // Load image with Glide
        if (currentItem.getImage() != null && !currentItem.getImage().isEmpty()) {
            try {
                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.ic_combined_placeholder)
                        .error(R.drawable.ic_combined_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                Glide.with(this)
                        .load(currentItem.getImage())
                        .apply(requestOptions)
                        .into(ivItemImage);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                ivItemImage.setImageResource(R.drawable.ic_combined_placeholder);
            }
        } else {
            ivItemImage.setImageResource(R.drawable.ic_combined_placeholder);
        }

        // Update claim button based on status
        if (currentItem.getStatus().equals("returned")) {
            btnClaim.setVisibility(View.GONE);
            btnContactReporter.setVisibility(View.GONE);
        } else if (currentItem.getStatus().equals("claimed")) {
            btnClaim.setText("Item Claimed - Waiting for Approval");
            btnClaim.setEnabled(false);
        }
    }

    private void claimItem() {
        long userId = sharedPreferences.getLong("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String claimProof = "User claims this item belongs to them";

        int result = databaseHelper.updateItemStatus(itemId, "claimed", userId, claimProof);
        if (result > 0) {
            Toast.makeText(this, "Item claimed successfully. Waiting for admin approval.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to claim item", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
}