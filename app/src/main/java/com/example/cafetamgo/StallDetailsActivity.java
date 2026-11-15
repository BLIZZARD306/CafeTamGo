package com.example.cafetamgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StallDetailsActivity extends AppCompatActivity {

    private TextView tvStallTitle, tvStallLocationInfo, tvStallRatingSummary;
    private RecyclerView rvStallMenu;
    private DatabaseHelper dbHelper;
    private int stallId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stall_details);

        dbHelper = new DatabaseHelper(this);
        tvStallTitle = findViewById(R.id.tvStallTitle);
        tvStallLocationInfo = findViewById(R.id.tvStallLocationInfo);
        tvStallRatingSummary = findViewById(R.id.tvStallRatingSummary);
        rvStallMenu = findViewById(R.id.rvStallMenu);

        rvStallMenu.setLayoutManager(new LinearLayoutManager(this));

        stallId = getIntent().getIntExtra("STALL_ID", -1);

        loadStallData();
    }

    private void loadStallData() {
        // Load Stall Header Info
        List<Stall> allStalls = dbHelper.getAllStalls();
        for (Stall s : allStalls) {
            if (s.getStallId() == stallId) {
                tvStallTitle.setText(s.getStallName());
                tvStallLocationInfo.setText(s.getLocation() + "\nOpen from " + s.getOpeningTime() + " to " + s.getClosingTime());
                tvStallRatingSummary.setText("★ " + String.format("%.1f", s.getAverageRating()) + " (" + s.getReviewCount() + " reviews)");
                break;
            }
        }

        // Load Menu Items for this Stall
        List<MenuItem> allMenuItems = dbHelper.getAllMenuItems();
        List<MenuItem> stallMenu = new ArrayList<>();
        for (MenuItem item : allMenuItems) {
            if (item.getStallId() == stallId) {
                stallMenu.add(item);
            }
        }

        StallMenuAdapter menuAdapter = new StallMenuAdapter(stallMenu, item -> {
            Intent intent = new Intent(StallDetailsActivity.this, ProductDetailsActivity.class);
            intent.putExtra("MENU_ITEM_ID", item.getMenuItemId());
            startActivity(intent);
        });
        rvStallMenu.setAdapter(menuAdapter);
    }
}