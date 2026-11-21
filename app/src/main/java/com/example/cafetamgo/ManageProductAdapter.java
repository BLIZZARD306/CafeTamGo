package com.example.cafetamgo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManageProductAdapter extends RecyclerView.Adapter<ManageProductAdapter.ViewHolder> {

    private List<MenuItem> productList;
    private OnEditProductClickListener editListener;

    public interface OnEditProductClickListener {
        void onEditProduct(MenuItem item);
    }

    public ManageProductAdapter(List<MenuItem> productList, OnEditProductClickListener editListener) {
        this.productList = productList;
        this.editListener = editListener;
    }

    public void updateList(List<MenuItem> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = productList.get(position);
        holder.tvFoodName.setText(item.getFoodName());

        double priceInPesos = item.getPrice() / 100.0;
        holder.tvFoodPrice.setText(String.format("Starts at ₱%.2f", priceInPesos));
        holder.tvFoodRating.setText("Edit");

        if (item.getIsAvailable() == 1) {
            holder.tvAvailabilityBadge.setText("Available");
            holder.tvAvailabilityBadge.setBackgroundColor(Color.parseColor("#0A8A0A"));
        } else {
            holder.tvAvailabilityBadge.setText("Unavailable");
            holder.tvAvailabilityBadge.setBackgroundColor(Color.parseColor("#7F8C8D"));
        }

        View.OnClickListener openEditor = v -> editListener.onEditProduct(item);
        holder.tvFoodRating.setOnClickListener(openEditor);
        holder.itemView.setOnClickListener(openEditor);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvFoodPrice, tvFoodRating, tvAvailabilityBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvFoodRating = itemView.findViewById(R.id.tvFoodRating);
            tvAvailabilityBadge = itemView.findViewById(R.id.tvAvailabilityBadge);
        }
    }
}