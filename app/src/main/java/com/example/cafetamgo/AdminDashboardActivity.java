package com.example.cafetamgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminGreeting, tvStallCount, tvProductCount, tvReportCount;
    private DatabaseHelper dbHelper;
    private int currentUserId = -1;
    private String adminName = "Tammy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DatabaseHelper(this);
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        String passedName = getIntent().getStringExtra("USER_NAME");
        if (passedName != null && !passedName.trim().isEmpty()) {
            adminName = passedName.trim();
        }

        tvAdminGreeting = findViewById(R.id.tvAdminGreeting);
        tvStallCount = findViewById(R.id.tvStallCount);
        tvProductCount = findViewById(R.id.tvProductCount);
        tvReportCount = findViewById(R.id.tvReportCount);

        tvAdminGreeting.setText("Let's keep things\nTam-tastic, " + adminName + "!");

        CardView cardManageStalls = findViewById(R.id.cardManageStalls);
        CardView cardManageProducts = findViewById(R.id.cardManageProducts);
        CardView cardReviewReports = findViewById(R.id.cardReviewReports);
        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);

        cardManageStalls.setOnClickListener(v -> openScreen(ManageStallsActivity.class));
        cardManageProducts.setOnClickListener(v -> openScreen(ManageProductsActivity.class));
        cardReviewReports.setOnClickListener(v -> openScreen(ReviewModerationActivity.class));

        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        refreshSummaryCounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSummaryCounts();
    }

    private void openScreen(Class<?> targetActivity) {
        Intent intent = new Intent(AdminDashboardActivity.this, targetActivity);
        intent.putExtra("USER_ID", currentUserId);
        intent.putExtra("USER_NAME", adminName);
        startActivity(intent);
    }

    private void refreshSummaryCounts() {
        tvStallCount.setText(String.valueOf(dbHelper.getStallCount()));
        tvProductCount.setText(String.valueOf(dbHelper.getProductCount()));
        tvReportCount.setText(String.valueOf(dbHelper.getPendingReportCount()));
    }
}