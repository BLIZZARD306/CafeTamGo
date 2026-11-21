package com.example.cafetamgo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StallMenuAdapter extends RecyclerView.Adapter<StallMenuAdapter.MenuViewHolder> {

    private List<MenuItem> menuList;
    private OnMenuItemClickListener listener;

    public interface OnMenuItemClickListener {
        void onMenuItemClick(MenuItem item);
    }

    public StallMenuAdapter(List<MenuItem> menuList, OnMenuItemClickListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuList.get(position);
        holder.tvFoodName.setText(item.getFoodName());

        double priceInPesos = item.getPrice() / 100.0;
        holder.tvFoodPrice.setText(String.format("Starts at ₱%.2f", priceInPesos));
        holder.tvFoodRating.setText("★ " + String.format("%.1f", item.getAverageRating()) + " (" + item.getReviewCount() + " reviews)");

        if (item.getIsAvailable() == 1) {
            holder.tvAvailabilityBadge.setText("Available");
            holder.tvAvailabilityBadge.setBackgroundColor(Color.parseColor("#27AE60"));
        } else {
            holder.tvAvailabilityBadge.setText("Unavailable");
            holder.tvAvailabilityBadge.setBackgroundColor(Color.parseColor("#7F8C8D"));
        }

        holder.itemView.setOnClickListener(v -> listener.onMenuItemClick(item));
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvFoodPrice, tvFoodRating, tvAvailabilityBadge;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvFoodRating = itemView.findViewById(R.id.tvFoodRating);
            tvAvailabilityBadge = itemView.findViewById(R.id.tvAvailabilityBadge);
        }
    }

    public void updateList(List<MenuItem> newList) {
        this.menuList = newList;
        notifyDataSetChanged();
    }
}