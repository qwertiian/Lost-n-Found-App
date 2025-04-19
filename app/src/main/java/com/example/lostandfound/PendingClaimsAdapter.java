package com.example.lostandfound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PendingClaimsAdapter extends RecyclerView.Adapter<PendingClaimsAdapter.ClaimViewHolder> {

    private List<Item> pendingClaims;
    private OnClaimActionListener listener;
    private Context context;

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
    public ClaimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_claim, parent, false);
        return new ClaimViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaimViewHolder holder, int position) {
        Item item = pendingClaims.get(position);

        holder.tvItemName.setText(item.getName());
        holder.tvClaimProof.setText("Claim proof: " + item.getClaimProof());

        holder.btnApprove.setOnClickListener(v -> listener.onApproveClaim(item));
        holder.btnReject.setOnClickListener(v -> listener.onRejectClaim(item));
    }

    @Override
    public int getItemCount() {
        return pendingClaims.size();
    }

    static class ClaimViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvClaimProof;
        Button btnApprove, btnReject;

        public ClaimViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvClaimProof = itemView.findViewById(R.id.tvClaimProof);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}