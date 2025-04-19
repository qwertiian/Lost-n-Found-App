package com.example.lostandfound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

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

        // In a real app, you would implement a way for the user to provide proof
        // For this example, we'll just use a simple text proof
        String claimProof = "User claims this item belongs to them";

        int result = databaseHelper.updateItemStatus(itemId, "claimed", userId, claimProof);
        if (result > 0) {
            Toast.makeText(this, "Item claimed successfully. Waiting for admin approval.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to claim item", Toast.LENGTH_SHORT).show();
        }
    }
}