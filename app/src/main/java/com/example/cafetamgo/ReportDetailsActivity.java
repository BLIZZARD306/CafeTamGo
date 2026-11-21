package com.example.cafetamgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReportDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ModerationReview item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        dbHelper = new DatabaseHelper(this);
        item = (ModerationReview) getIntent().getSerializableExtra("MODERATION_REVIEW");

        ImageButton btnBack = findViewById(R.id.btnBackReportDetails);
        TextView tvFoodName = findViewById(R.id.tvDetailFoodName);
        TextView tvStallName = findViewById(R.id.tvDetailStallName);
        TextView tvReviewerName = findViewById(R.id.tvDetailReviewerName);
        TextView tvStars = findViewById(R.id.tvDetailStars);
        TextView tvComment = findViewById(R.id.tvDetailComment);
        TextView tvViewProduct = findViewById(R.id.tvViewProduct);

        TextView tvReportsReceived = findViewById(R.id.tvReportsReceived);
        TextView tvReportReasons = findViewById(R.id.tvReportReasons);
        TextView tvReporterExplanation = findViewById(R.id.tvReporterExplanation);

        Button btnHideReview = findViewById(R.id.btnHideReview);
        TextView btnDismissReport = findViewById(R.id.btnDismissReport);

        btnBack.setOnClickListener(v -> finish());

        if (item != null) {
            tvFoodName.setText(item.getFoodName());
            tvStallName.setText(item.getStallName());
            tvReviewerName.setText(item.getReviewerName());
            tvStars.setText(buildStars(item.getRating()));
            tvComment.setText(item.getComment() != null ? item.getComment() : "");

            tvReportsReceived.setText(String.valueOf(Math.max(1, item.getReportCount())));
            tvReportReasons.setText(item.getReasons() != null ? item.getReasons() : "Reported content");
            tvReporterExplanation.setText(item.getExplanation() != null ? item.getExplanation() : "No explanation provided.");

            tvViewProduct.setOnClickListener(v -> {
                Intent intent = new Intent(ReportDetailsActivity.this, ProductDetailsActivity.class);
                intent.putExtra("MENU_ITEM_ID", item.getMenuItemId());
                startActivity(intent);
            });

            btnHideReview.setOnClickListener(v -> {
                if (dbHelper.hideReview(item.getReviewId())) {
                    Toast.makeText(this, "Review hidden", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            btnDismissReport.setOnClickListener(v -> {
                if (dbHelper.dismissReportsForReview(item.getReviewId())) {
                    Toast.makeText(this, "Report dismissed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
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
}