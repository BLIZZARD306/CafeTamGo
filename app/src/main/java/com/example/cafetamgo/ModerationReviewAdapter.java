package com.example.cafetamgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ModerationReviewAdapter extends RecyclerView.Adapter<ModerationReviewAdapter.ViewHolder> {

    public interface OnReviewActionListener {
        void onReviewReport(ModerationReview item);
        void onRestoreReview(ModerationReview item);
    }

    private List<ModerationReview> items;
    private boolean isHiddenTab;
    private OnReviewActionListener listener;

    public ModerationReviewAdapter(List<ModerationReview> items, boolean isHiddenTab, OnReviewActionListener listener) {
        this.items = items;
        this.isHiddenTab = isHiddenTab;
        this.listener = listener;
    }

    public void setData(List<ModerationReview> items, boolean isHiddenTab) {
        this.items = items;
        this.isHiddenTab = isHiddenTab;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moderation_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModerationReview item = items.get(position);
        holder.tvModFoodName.setText(item.getFoodName());
        holder.tvModStallName.setText(item.getStallName());
        holder.tvModReviewerName.setText(item.getReviewerName());
        holder.tvModStars.setText(buildStars(item.getRating()));
        holder.tvModComment.setText(item.getComment() != null ? item.getComment() : "");

        if (isHiddenTab) {
            holder.btnModAction.setText("Restore Review");
            holder.btnModAction.setOnClickListener(v -> listener.onRestoreReview(item));
        } else {
            holder.btnModAction.setText("Review Report");
            holder.btnModAction.setOnClickListener(v -> listener.onReviewReport(item));
        }
    }

    private String buildStars(int rating) {
        int clamped = Math.max(1, Math.min(5, rating));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < clamped ? "★" : "☆");
        }
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvModFoodName, tvModStallName, tvModReviewerName, tvModStars, tvModComment;
        Button btnModAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModFoodName = itemView.findViewById(R.id.tvModFoodName);
            tvModStallName = itemView.findViewById(R.id.tvModStallName);
            tvModReviewerName = itemView.findViewById(R.id.tvModReviewerName);
            tvModStars = itemView.findViewById(R.id.tvModStars);
            tvModComment = itemView.findViewById(R.id.tvModComment);
            btnModAction = itemView.findViewById(R.id.btnModAction);
        }
    }
}