package com.example.cafetamgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {

    private List<MenuItem> menuList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    public RecommendationAdapter(List<MenuItem> menuList, OnItemClickListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommendation, parent, false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        MenuItem item = menuList.get(position);
        holder.tvFoodName.setText(item.getFoodName());
        holder.tvStallSource.setText(item.getStallName());

        double priceInPesos = item.getPrice() / 100.0;
        holder.tvFoodPrice.setText(String.format("₱%.2f", priceInPesos));
        holder.tvFoodRating.setText("★ " + String.format("%.1f", item.getAverageRating()));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvStallSource, tvFoodPrice, tvFoodRating;

        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvStallSource = itemView.findViewById(R.id.tvStallSource);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvFoodRating = itemView.findViewById(R.id.tvFoodRating);
        }
    }
}