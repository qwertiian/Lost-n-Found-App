package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ItemsListActivity extends AppCompatActivity implements ItemsAdapter.OnItemClickListener {

    private RecyclerView rvItems;
    private FloatingActionButton fabFilter;
    private DatabaseHelper databaseHelper;
    private ItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        rvItems = findViewById(R.id.rvItems);
        fabFilter = findViewById(R.id.fabFilter);
        databaseHelper = new DatabaseHelper(this);

        setupRecyclerView();

        fabFilter.setOnClickListener(v -> {
            // Implement filter functionality
        });
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView(); // Refresh the list when returning from detail view
    }
}