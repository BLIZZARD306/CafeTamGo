package com.example.cafetamgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductReviewAdapter extends RecyclerView.Adapter<ProductReviewAdapter.ViewHolder> {

    public interface OnReportReviewClickListener {
        void onReportReview(ProductReviewItem review);
    }

    private List<ProductReviewItem> reviews;
    private OnReportReviewClickListener listener;

    public ProductReviewAdapter(List<ProductReviewItem> reviews, OnReportReviewClickListener listener) {
        this.reviews = reviews;
        this.listener = listener;
    }

    public void updateList(List<ProductReviewItem> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductReviewItem review = reviews.get(position);
        holder.tvReviewAuthor.setText(review.getReviewerName());
        holder.tvReviewRatingLine.setText("★ " + review.getRating() + "/5");
        holder.tvReviewComment.setText(review.getComment() != null ? review.getComment() : "");
        holder.tvReviewDate.setText(review.getReviewDate() != null ? review.getReviewDate() : "");
        holder.tvReportReview.setOnClickListener(v -> listener.onReportReview(review));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewAuthor, tvReviewRatingLine, tvReviewComment, tvReviewDate, tvReportReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewAuthor = itemView.findViewById(R.id.tvReviewAuthor);
            tvReviewRatingLine = itemView.findViewById(R.id.tvReviewRatingLine);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvReportReview = itemView.findViewById(R.id.tvReportReview);
        }
    }
}