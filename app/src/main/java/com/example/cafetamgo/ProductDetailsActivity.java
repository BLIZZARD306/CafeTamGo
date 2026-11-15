package com.example.cafetamgo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductPrice, tvProductStallSource, tvProductRating, tvProductDescription;
    private EditText etRating, etComment;
    private Button btnSubmitReview;
    private ListView lvReviews;
    private DatabaseHelper dbHelper;
    private int menuItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        dbHelper = new DatabaseHelper(this);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductStallSource = findViewById(R.id.tvProductStallSource);
        tvProductRating = findViewById(R.id.tvProductRating);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        etRating = findViewById(R.id.etRating);
        etComment = findViewById(R.id.etComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        lvReviews = findViewById(R.id.lvReviews);

        menuItemId = getIntent().getIntExtra("MENU_ITEM_ID", -1);

        loadProductDetails();
        loadReviews();

        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void loadProductDetails() {
        List<MenuItem> allItems = dbHelper.getAllMenuItems();
        boolean found = false;
        for (MenuItem item : allItems) {
            if (item.getMenuItemId() == menuItemId) {
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

        // Debug toast if it couldn't find the item
        if (!found && menuItemId != -1) {
            Toast.makeText(this, "Could not load item details for ID: " + menuItemId, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadReviews() {
        List<String> reviews = dbHelper.getReviewsForProduct(menuItemId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reviews);
        lvReviews.setAdapter(adapter);
    }

    private void submitReview() {
        String ratingStr = etRating.getText().toString().trim();
        String comment = etComment.getText().toString().trim();

        if (ratingStr.isEmpty()) {
            Toast.makeText(this, "Please enter a rating between 1 and 5", Toast.LENGTH_SHORT).show();
            return;
        }

        int rating = Integer.parseInt(ratingStr);
        if (rating < 1 || rating > 5) {
            Toast.makeText(this, "Rating must be 1 to 5 stars", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        // Using userId = 1 as active session stub
        boolean success = dbHelper.addReview(1, null, menuItemId, rating, comment, currentDate);

        if (success) {
            Toast.makeText(this, "Review posted successfully!", Toast.LENGTH_SHORT).show();
            etRating.setText("");
            etComment.setText("");
            loadReviews();
            loadProductDetails(); // Refresh rating averages
        } else {
            Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show();
        }
    }
}