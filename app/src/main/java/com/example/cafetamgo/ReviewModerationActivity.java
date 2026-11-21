package com.example.cafetamgo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReviewModerationActivity extends AppCompatActivity {

    private TextView tvTabReported, tvTabHidden;
    private View indicatorReported, indicatorHidden;
    private RecyclerView rvModerationReviews;
    private DatabaseHelper dbHelper;
    private ModerationReviewAdapter adapter;
    private boolean showingHidden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_moderation);

        dbHelper = new DatabaseHelper(this);
        ImageButton btnBack = findViewById(R.id.btnBackModeration);
        LinearLayout tabReported = findViewById(R.id.tabReported);
        LinearLayout tabHidden = findViewById(R.id.tabHidden);
        tvTabReported = findViewById(R.id.tvTabReported);
        tvTabHidden = findViewById(R.id.tvTabHidden);
        indicatorReported = findViewById(R.id.indicatorReported);
        indicatorHidden = findViewById(R.id.indicatorHidden);
        rvModerationReviews = findViewById(R.id.rvModerationReviews);

        btnBack.setOnClickListener(v -> finish());

        rvModerationReviews.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ModerationReviewAdapter(new ArrayList<>(), false, new ModerationReviewAdapter.OnReviewActionListener() {
            @Override
            public void onReviewReport(ModerationReview item) {
                Intent intent = new Intent(ReviewModerationActivity.this, ReportDetailsActivity.class);
                intent.putExtra("MODERATION_REVIEW", item);
                startActivity(intent);
            }

            @Override
            public void onRestoreReview(ModerationReview item) {
                showRestoreConfirmDialog(item);
            }
        });
        rvModerationReviews.setAdapter(adapter);

        tabReported.setOnClickListener(v -> {
            showingHidden = false;
            refreshTabsAndList();
        });

        tabHidden.setOnClickListener(v -> {
            showingHidden = true;
            refreshTabsAndList();
        });

        refreshTabsAndList();
    }

    private void refreshTabsAndList() {
        List<ModerationReview> reportedList = dbHelper.getReportedReviews();
        List<ModerationReview> hiddenList = dbHelper.getHiddenReviews();

        tvTabReported.setText("Reported (" + reportedList.size() + ")");
        tvTabHidden.setText("Hidden (" + hiddenList.size() + ")");

        if (showingHidden) {
            tvTabHidden.setTextColor(Color.parseColor("#D35400"));
            indicatorHidden.setBackgroundColor(Color.parseColor("#D35400"));
            indicatorHidden.getLayoutParams().height = dpToPx(2);

            tvTabReported.setTextColor(Color.parseColor("#1C1C1C"));
            indicatorReported.setBackgroundColor(Color.parseColor("#CCCCCC"));
            indicatorReported.getLayoutParams().height = dpToPx(1);

            adapter.setData(hiddenList, true);
        } else {
            tvTabReported.setTextColor(Color.parseColor("#D35400"));
            indicatorReported.setBackgroundColor(Color.parseColor("#D35400"));
            indicatorReported.getLayoutParams().height = dpToPx(2);

            tvTabHidden.setTextColor(Color.parseColor("#1C1C1C"));
            indicatorHidden.setBackgroundColor(Color.parseColor("#CCCCCC"));
            indicatorHidden.getLayoutParams().height = dpToPx(1);

            adapter.setData(reportedList, false);
        }
        indicatorReported.requestLayout();
        indicatorHidden.requestLayout();
    }

    private void showRestoreConfirmDialog(ModerationReview item) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_restore_review, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        Button btnConfirmRestore = dialogView.findViewById(R.id.btnConfirmRestore);
        Button btnCancelRestore = dialogView.findViewById(R.id.btnCancelRestore);

        btnConfirmRestore.setOnClickListener(v -> {
            if (dbHelper.restoreReview(item.getReviewId())) {
                Toast.makeText(this, "Review restored", Toast.LENGTH_SHORT).show();
                refreshTabsAndList();
            }
            dialog.dismiss();
        });

        btnCancelRestore.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTabsAndList();
    }
}