package com.example.lostandfound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ItemsListActivity extends AppCompatActivity implements ItemsAdapter.OnItemClickListener {

    private RecyclerView rvItems;
    private FloatingActionButton fabFilter;
    private DatabaseHelper databaseHelper;
    private ItemsAdapter adapter;
    private Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Lost & Found Items");
        }

        rvItems = findViewById(R.id.rvItems);
        fabFilter = findViewById(R.id.fabFilter);
        databaseHelper = new DatabaseHelper(this);

        setupRecyclerView();

        fabFilter.setOnClickListener(v -> {
            // Implement filter functionality
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

    private void setupRecyclerView() {
        List<Item> items = databaseHelper.getAllItems();
        adapter = new ItemsAdapter(items, this);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        Item item = adapter.getItemAt(position);
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra("itemId", item.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView(); // Refresh the list when returning from detail view
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
}