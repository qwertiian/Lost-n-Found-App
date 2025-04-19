package com.example.lostandfound;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private List<Item> items;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ItemsAdapter(List<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);

        holder.tvItemName.setText(item.getName());
        holder.tvItemDate.setText(item.getDate());

        // Set status text and color
        String status = item.getStatus();
        holder.tvItemStatus.setText(status);

        switch (status) {
            case "lost":
                holder.tvItemStatus.setBackgroundColor(Color.YELLOW);
                break;
            case "claimed":
                holder.tvItemStatus.setBackgroundColor(Color.BLUE);
                break;
            case "returned":
                holder.tvItemStatus.setBackgroundColor(Color.GREEN);
                holder.viewStrikethrough.setVisibility(View.VISIBLE);
                break;
        }

        // Load image using Glide
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            Glide.with(context)
                    .load(item.getImage())
                    .placeholder(R.drawable.ic_combined_placeholder)
                    .into(holder.ivItemImage);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Item getItemAt(int position) {
        return items.get(position);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        TextView tvItemName, tvItemDate, tvItemStatus;
        View viewStrikethrough;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDate = itemView.findViewById(R.id.tvItemDate);
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);
            viewStrikethrough = itemView.findViewById(R.id.viewStrikethrough);
        }
    }
}