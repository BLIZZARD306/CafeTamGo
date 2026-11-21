package com.example.cafetamgo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManageStallAdapter extends RecyclerView.Adapter<ManageStallAdapter.ViewHolder> {

    private List<Stall> stallList;
    private StallActionListener listener;

    public interface StallActionListener {
        void onEditStall(Stall stall);
        void onViewProducts(Stall stall);
    }

    public ManageStallAdapter(List<Stall> stallList, StallActionListener listener) {
        this.stallList = stallList;
        this.listener = listener;
    }

    public void updateList(List<Stall> newList) {
        this.stallList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_stall, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stall stall = stallList.get(position);
        holder.tvManageStallName.setText(stall.getStallName());
        holder.tvManageStallLocation.setText(stall.getLocation());
        holder.tvManageStallHours.setText("Open from " + stall.getOpeningTime() + " to " + stall.getClosingTime());

        if (stall.getIsOpen() == 1) {
            holder.tvManageStallStatus.setText("Open");
            holder.tvManageStallStatus.setBackgroundColor(Color.parseColor("#0A8A0A"));
        } else {
            holder.tvManageStallStatus.setText("Closed");
            holder.tvManageStallStatus.setBackgroundColor(Color.parseColor("#A80000"));
        }

        holder.tvEditStall.setOnClickListener(v -> listener.onEditStall(stall));
        holder.tvViewProducts.setOnClickListener(v -> listener.onViewProducts(stall));
    }

    @Override
    public int getItemCount() {
        return stallList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvManageStallName, tvManageStallStatus, tvManageStallLocation, tvManageStallHours;
        TextView tvEditStall, tvViewProducts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvManageStallName = itemView.findViewById(R.id.tvManageStallName);
            tvManageStallStatus = itemView.findViewById(R.id.tvManageStallStatus);
            tvManageStallLocation = itemView.findViewById(R.id.tvManageStallLocation);
            tvManageStallHours = itemView.findViewById(R.id.tvManageStallHours);
            tvEditStall = itemView.findViewById(R.id.tvEditStall);
            tvViewProducts = itemView.findViewById(R.id.tvViewProducts);
        }
    }
}