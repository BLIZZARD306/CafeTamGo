package com.example.cafetamgo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StallAdapter extends RecyclerView.Adapter<StallAdapter.StallViewHolder> {

    private List<Stall> stallList;
    private OnStallClickListener listener;

    public interface OnStallClickListener {
        void onStallClick(Stall stall);
    }

    public StallAdapter(List<Stall> stallList, OnStallClickListener listener) {
        this.stallList = stallList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stall, parent, false);
        return new StallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StallViewHolder holder, int position) {
        Stall stall = stallList.get(position);
        holder.tvStallName.setText(stall.getStallName());
        holder.tvStallLocation.setText(stall.getLocation());
        holder.tvStallHours.setText("Open from " + stall.getOpeningTime() + " to " + stall.getClosingTime());
        holder.tvStallRating.setText("★ " + String.format("%.1f", stall.getAverageRating()) + " (" + stall.getReviewCount() + " reviews)");

        if (stall.getIsOpen() == 1) {
            holder.tvOpenBadge.setText("Open");
            holder.tvOpenBadge.setBackgroundColor(Color.parseColor("#27AE60"));
        } else {
            holder.tvOpenBadge.setText("Closed");
            holder.tvOpenBadge.setBackgroundColor(Color.parseColor("#C0392B"));
        }

        holder.itemView.setOnClickListener(v -> listener.onStallClick(stall));
    }

    @Override
    public int getItemCount() {
        return stallList.size();
    }

    public static class StallViewHolder extends RecyclerView.ViewHolder {
        TextView tvStallName, tvStallLocation, tvStallHours, tvStallRating, tvOpenBadge;

        public StallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStallName = itemView.findViewById(R.id.tvStallName);
            tvStallLocation = itemView.findViewById(R.id.tvStallLocation);
            tvStallHours = itemView.findViewById(R.id.tvStallHours);
            tvStallRating = itemView.findViewById(R.id.tvStallRating);
            tvOpenBadge = itemView.findViewById(R.id.tvOpenBadge);
        }
    }

    public void updateList(List<Stall> newList) {
        this.stallList = newList;
        notifyDataSetChanged();
    }
}