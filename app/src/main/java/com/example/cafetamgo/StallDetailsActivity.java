package com.example.cafetamgo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StallDetailsActivity extends AppCompatActivity {

    private TextView tvStallTitle, tvStallLocationInfo, tvStallRatingSummary;
    private EditText etSearchMenu;
    private Button btnSortMenu, btnAvailableNow;
    private Button chipAll, chipRiceMeals, chipPasta, chipDrinks;
    private RecyclerView rvStallMenu;
    private StallMenuAdapter menuAdapter;
    private DatabaseHelper dbHelper;

    private int stallId;
    private int currentUserId = -1;
    private String currentUserName = "Customer";

    private List<MenuItem> stallMenuItems = new ArrayList<>();
    private String selectedCategory = "All";
    private boolean onlyAvailable = false;
    private int sortOptionIndex = 0; // 0: Default, 1: Highest Reviews, 2: Lowest Reviews, 3: Price Low-High, 4: A-Z

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stall_details);

        dbHelper = new DatabaseHelper(this);
        ImageButton btnBack = findViewById(R.id.btnBackStallDetails);
        tvStallTitle = findViewById(R.id.tvStallTitle);
        tvStallLocationInfo = findViewById(R.id.tvStallLocationInfo);
        tvStallRatingSummary = findViewById(R.id.tvStallRatingSummary);
        etSearchMenu = findViewById(R.id.etSearchMenu);
        btnSortMenu = findViewById(R.id.btnSortMenu);
        btnAvailableNow = findViewById(R.id.btnAvailableNow);
        chipAll = findViewById(R.id.chipAll);
        chipRiceMeals = findViewById(R.id.chipRiceMeals);
        chipPasta = findViewById(R.id.chipPasta);
        chipDrinks = findViewById(R.id.chipDrinks);
        rvStallMenu = findViewById(R.id.rvStallMenu);

        btnBack.setOnClickListener(v -> finish());

        stallId = getIntent().getIntExtra("STALL_ID", -1);
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        String passedName = getIntent().getStringExtra("USER_NAME");
        if (passedName != null && !passedName.trim().isEmpty()) {
            currentUserName = passedName.trim();
        } else if (currentUserId != -1) {
            String fetched = dbHelper.getUserNameById(currentUserId);
            if (fetched != null && !fetched.trim().isEmpty()) {
                currentUserName = fetched.trim();
            }
        }

        rvStallMenu.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new StallMenuAdapter(new ArrayList<>(), item -> {
            Intent intent = new Intent(StallDetailsActivity.this, ProductDetailsActivity.class);
            intent.putExtra("MENU_ITEM_ID", item.getMenuItemId());
            intent.putExtra("USER_ID", currentUserId);
            intent.putExtra("USER_NAME", currentUserName);
            startActivity(intent);
        });
        rvStallMenu.setAdapter(menuAdapter);

        setupCategoryChips();
        setupSearchSortAndFilter();
        loadStallData();
    }

    private void setupCategoryChips() {
        chipAll.setOnClickListener(v -> selectCategory("All", chipAll));
        chipRiceMeals.setOnClickListener(v -> selectCategory("Rice Meals", chipRiceMeals));
        chipPasta.setOnClickListener(v -> selectCategory("Pasta", chipPasta));
        chipDrinks.setOnClickListener(v -> selectCategory("Drinks", chipDrinks));
    }

    private void selectCategory(String category, Button activeChip) {
        selectedCategory = category;
        Button[] allChips = {chipAll, chipRiceMeals, chipPasta, chipDrinks};
        for (Button chip : allChips) {
            boolean isSelected = (chip == activeChip);
            chip.setBackgroundResource(isSelected ? R.drawable.bg_category_chip_selected : R.drawable.bg_category_chip_unselected);
            chip.setTextColor(Color.parseColor(isSelected ? "#FFFFFF" : "#1C1C1C"));
        }
        applyMenuFiltersAndSort();
    }

    private void setupSearchSortAndFilter() {
        etSearchMenu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyMenuFiltersAndSort();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnAvailableNow.setOnClickListener(v -> {
            onlyAvailable = !onlyAvailable;
            btnAvailableNow.setText(onlyAvailable ? "✓ Available now" : "Available now");
            applyMenuFiltersAndSort();
        });

        btnSortMenu.setOnClickListener(v -> {
            String[] options = {"Default", "Highest Reviews", "Lowest Reviews", "Price: Low to High", "A–Z"};
            new AlertDialog.Builder(this)
                    .setTitle("Sort Menu")
                    .setSingleChoiceItems(options, sortOptionIndex, (dialog, which) -> {
                        sortOptionIndex = which;
                        applyMenuFiltersAndSort();
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    private void loadStallData() {
        List<Stall> allStalls = dbHelper.getAllStalls();
        for (Stall s : allStalls) {
            if (s.getStallId() == stallId) {
                tvStallTitle.setText(s.getStallName());
                tvStallLocationInfo.setText(s.getLocation() + "\nOpen from " + s.getOpeningTime() + " to " + s.getClosingTime());
                tvStallRatingSummary.setText("★ " + String.format("%.1f", s.getAverageRating()) + " (" + s.getReviewCount() + " reviews)");
                break;
            }
        }

        List<MenuItem> allMenuItems = dbHelper.getAllMenuItems();
        stallMenuItems.clear();
        for (MenuItem item : allMenuItems) {
            if (item.getStallId() == stallId) {
                stallMenuItems.add(item);
            }
        }

        applyMenuFiltersAndSort();
    }

    private void applyMenuFiltersAndSort() {
        String query = etSearchMenu.getText() != null ? etSearchMenu.getText().toString().trim().toLowerCase() : "";
        List<MenuItem> filtered = new ArrayList<>();

        for (MenuItem item : stallMenuItems) {
            if (onlyAvailable && item.getIsAvailable() != 1) {
                continue;
            }
            if (!"All".equalsIgnoreCase(selectedCategory)) {
                String cat = item.getCategory() != null ? item.getCategory() : "";
                if (!cat.equalsIgnoreCase(selectedCategory)) {
                    continue;
                }
            }
            String food = item.getFoodName() != null ? item.getFoodName().toLowerCase() : "";
            String desc = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
            if (query.isEmpty() || food.contains(query) || desc.contains(query)) {
                filtered.add(item);
            }
        }

        switch (sortOptionIndex) {
            case 1:
                Collections.sort(filtered, (a, b) -> Integer.compare(b.getReviewCount(), a.getReviewCount()));
                break;
            case 2:
                Collections.sort(filtered, (a, b) -> Integer.compare(a.getReviewCount(), b.getReviewCount()));
                break;
            case 3:
                Collections.sort(filtered, (a, b) -> Integer.compare(a.getPrice(), b.getPrice()));
                break;
            case 4:
                Collections.sort(filtered, (a, b) -> {
                    String nameA = a.getFoodName() != null ? a.getFoodName() : "";
                    String nameB = b.getFoodName() != null ? b.getFoodName() : "";
                    return nameA.compareToIgnoreCase(nameB);
                });
                break;
            default:
                break;
        }

        menuAdapter.updateList(filtered);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStallData();
    }
}