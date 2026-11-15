package com.example.cafetamgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvRecommendations, rvStalls;
    private DatabaseHelper dbHelper;
    private Button btnSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);
        rvRecommendations = findViewById(R.id.rvRecommendations);
        rvStalls = findViewById(R.id.rvStalls);
        btnSort = findViewById(R.id.btnSort);

        // Set layout managers for both lists
        rvRecommendations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvStalls.setLayoutManager(new LinearLayoutManager(this));

        loadDashboardData();

        btnSort.setOnClickListener(v -> {
            Toast.makeText(this, "Sorting Stalls", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadDashboardData() {
        // 1. Load Stalls
        List<Stall> stallList = dbHelper.getAllStalls();
        StallAdapter stallAdapter = new StallAdapter(stallList, stall -> {
            Intent intent = new Intent(HomeActivity.this, StallDetailsActivity.class);
            intent.putExtra("STALL_ID", stall.getStallId());
            startActivity(intent);
        });
        rvStalls.setAdapter(stallAdapter);

        // 2. Load Recommendations (Menu Items)
        List<MenuItem> menuList = dbHelper.getAllMenuItems();
        RecommendationAdapter recommendationAdapter = new RecommendationAdapter(menuList, item -> {
            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
            intent.putExtra("MENU_ITEM_ID", item.getMenuItemId());
            startActivity(intent);
        });
        rvRecommendations.setAdapter(recommendationAdapter);
    }
}