package com.example.lostandfound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PendingClaimsAdapter extends RecyclerView.Adapter<PendingClaimsAdapter.ViewHolder> {

    private List<Item> pendingClaims;
    private OnClaimActionListener listener;

    public interface OnClaimActionListener {
        void onApproveClaim(Item item);
        void onRejectClaim(Item item);
    }

    public PendingClaimsAdapter(List<Item> pendingClaims, OnClaimActionListener listener) {
        this.pendingClaims = pendingClaims;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_claim, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = pendingClaims.get(position);
        holder.tvItemName.setText(item.getName());
        holder.tvItemDescription.setText(item.getDescription());
        holder.tvClaimerInfo.setText("Claimed by user ID: " + item.getClaimerId());
        holder.tvClaimProof.setText("Proof: " + item.getClaimProof());

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApproveClaim(item);
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRejectClaim(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingClaims.size();
    }

    public void updateData(List<Item> newItems) {
        pendingClaims = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemDescription, tvClaimerInfo, tvClaimProof;
        TextView btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDescription = itemView.findViewById(R.id.tvItemDescription);
            tvClaimerInfo = itemView.findViewById(R.id.tvClaimerInfo);
            tvClaimProof = itemView.findViewById(R.id.tvClaimProof);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}