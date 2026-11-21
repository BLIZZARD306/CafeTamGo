package com.example.cafetamgo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductPrice, tvProductStallSource, tvProductRating, tvProductDescription;
    private EditText etRating, etComment;
    private Button btnSubmitReview;
    private RecyclerView rvProductReviews;
    private ProductReviewAdapter reviewAdapter;
    private DatabaseHelper dbHelper;

    private int menuItemId = -1;
    private Integer stallIdForItem = null;
    private int currentUserId = -1;
    private String currentUserName = "Customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        dbHelper = new DatabaseHelper(this);
        ImageButton btnBack = findViewById(R.id.btnBackProductDetails);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductStallSource = findViewById(R.id.tvProductStallSource);
        tvProductRating = findViewById(R.id.tvProductRating);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        etRating = findViewById(R.id.etRating);
        etComment = findViewById(R.id.etComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        rvProductReviews = findViewById(R.id.rvProductReviews);

        btnBack.setOnClickListener(v -> finish());

        menuItemId = getIntent().getIntExtra("MENU_ITEM_ID", -1);
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        currentUserName = getIntent().getStringExtra("USER_NAME");

        if (currentUserId != -1 && (currentUserName == null || currentUserName.trim().isEmpty())) {
            String fetched = dbHelper.getUserNameById(currentUserId);
            if (fetched != null && !fetched.trim().isEmpty()) {
                currentUserName = fetched;
            }
        }
        if (currentUserName == null || currentUserName.trim().isEmpty()) {
            currentUserName = "Customer";
        }

        rvProductReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ProductReviewAdapter(new ArrayList<>(), this::showReportDialog);
        rvProductReviews.setAdapter(reviewAdapter);

        loadProductDetails();
        loadReviews();

        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void loadProductDetails() {
        List<MenuItem> allItems = dbHelper.getAllMenuItems();
        boolean found = false;
        for (MenuItem item : allItems) {
            if (item.getMenuItemId() == menuItemId) {
                stallIdForItem = item.getStallId();
                tvProductName.setText(item.getFoodName());
                double pricePesos = item.getPrice() / 100.0;
                tvProductPrice.setText(String.format("₱%.2f", pricePesos));
                tvProductStallSource.setText("Sold by: " + item.getStallName());
                tvProductRating.setText("★ " + String.format("%.1f", item.getAverageRating()) + " (" + item.getReviewCount() + " reviews)");
                tvProductDescription.setText(item.getDescription() != null ? item.getDescription() : "No description available.");
                found = true;
                break;
            }
        }

        if (!found && menuItemId != -1) {
            Toast.makeText(this, "Could not load item details for ID: " + menuItemId, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadReviews() {
        reviewAdapter.updateList(dbHelper.getProductReviewItems(menuItemId));
    }

    private void submitReview() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Please log in again to submit a review", Toast.LENGTH_SHORT).show();
            return;
        }

        String ratingStr = etRating.getText().toString().trim();
        String comment = etComment.getText().toString().trim();

        if (ratingStr.isEmpty()) {
            Toast.makeText(this, "Please enter a rating between 1 and 5", Toast.LENGTH_SHORT).show();
            return;
        }

        int rating;
        try {
            rating = Integer.parseInt(ratingStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Rating must be a number from 1 to 5", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating < 1 || rating > 5) {
            Toast.makeText(this, "Rating must be 1 to 5 stars", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        boolean success = dbHelper.addReview(currentUserId, stallIdForItem, menuItemId, rating, comment, currentDate);

        if (success) {
            Toast.makeText(this, "Review posted as " + currentUserName, Toast.LENGTH_SHORT).show();
            etRating.setText("");
            etComment.setText("");
            loadReviews();
            loadProductDetails();
        } else {
            Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show();
        }
    }

    private void showReportDialog(ProductReviewItem review) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = Math.round(16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, 0);

        String[] reasons = {"Abusive language", "Spam or irrelevant", "Misleading information", "Other"};
        Spinner spReason = new Spinner(this);
        spReason.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, reasons));

        EditText etExplanation = new EditText(this);
        etExplanation.setHint("Explain why this review should be reviewed...");
        etExplanation.setMinLines(2);

        container.addView(spReason);
        container.addView(etExplanation);

        new AlertDialog.Builder(this)
                .setTitle("Report Review by " + review.getReviewerName())
                .setView(container)
                .setPositiveButton("Submit Report", (dialog, which) -> {
                    String selectedReason = (String) spReason.getSelectedItem();
                    String explanation = etExplanation.getText().toString().trim();
                    int reporter = currentUserId != -1 ? currentUserId : 1;
                    if (dbHelper.reportReview(review.getReviewId(), reporter, selectedReason, explanation)) {
                        Toast.makeText(this, "Report submitted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Could not submit report", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}