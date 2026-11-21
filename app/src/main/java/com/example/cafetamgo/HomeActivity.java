package com.example.cafetamgo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvRecommendations, rvStalls;
    private EditText etSearchHome;
    private Button btnSort, btnFilter;
    private TextView tvHomeGreeting;
    private DatabaseHelper dbHelper;

    private StallAdapter stallAdapter;
    private RecommendationAdapter recommendationAdapter;

    private List<Stall> allStalls = new ArrayList<>();
    private List<MenuItem> allMenuItems = new ArrayList<>();

    private boolean onlyOpenStalls = false;
    private boolean sortByRatingDesc = false;

    private int currentUserId = -1;
    private String currentUserName = "Customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);
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

        tvHomeGreeting = findViewById(R.id.tvHomeGreeting);
        etSearchHome = findViewById(R.id.etSearchHome);
        rvRecommendations = findViewById(R.id.rvRecommendations);
        rvStalls = findViewById(R.id.rvStalls);
        btnSort = findViewById(R.id.btnSort);
        btnFilter = findViewById(R.id.btnFilter);

        LinearLayout navMyReviews = findViewById(R.id.navMyReviews);
        LinearLayout navBackToLogin = findViewById(R.id.navBackToLogin);

        setGreetingText(currentUserName);

        rvRecommendations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvStalls.setLayoutManager(new LinearLayoutManager(this));

        stallAdapter = new StallAdapter(new ArrayList<>(), stall -> {
            Intent intent = new Intent(HomeActivity.this, StallDetailsActivity.class);
            intent.putExtra("STALL_ID", stall.getStallId());
            intent.putExtra("USER_ID", currentUserId);
            intent.putExtra("USER_NAME", currentUserName);
            startActivity(intent);
        });
        rvStalls.setAdapter(stallAdapter);

        recommendationAdapter = new RecommendationAdapter(new ArrayList<>(), item -> {
            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
            intent.putExtra("MENU_ITEM_ID", item.getMenuItemId());
            intent.putExtra("USER_ID", currentUserId);
            intent.putExtra("USER_NAME", currentUserName);
            startActivity(intent);
        });
        rvRecommendations.setAdapter(recommendationAdapter);

        etSearchHome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFiltersAndSort();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnSort.setOnClickListener(v -> {
            String[] sortOptions = {
                    "Default",
                    "Highest Reviews",
                    "Lowest Reviews",
                    "A–Z",
                    "Z–A"
            };
            int checkedIndex = currentSortMode.ordinal();

            new AlertDialog.Builder(this)
                    .setTitle("Sort Food Stalls")
                    .setSingleChoiceItems(sortOptions, checkedIndex, (dialog, which) -> {
                        currentSortMode = SortMode.values()[which];
                        btnSort.setText(currentSortMode.label);
                        applyFiltersAndSort();
                        dialog.dismiss();
                    })
                    .show();
        });

        btnFilter.setOnClickListener(v -> {
            String[] options = {"All Stalls", "Open Stalls Only"};
            int checked = onlyOpenStalls ? 1 : 0;
            new AlertDialog.Builder(this)
                    .setTitle("Filter Food Stalls")
                    .setSingleChoiceItems(options, checked, (dialog, which) -> {
                        onlyOpenStalls = (which == 1);
                        btnFilter.setText(onlyOpenStalls ? "▼ Open" : "▼ Filter");
                        applyFiltersAndSort();
                        dialog.dismiss();
                    })
                    .show();
        });

        navMyReviews.setOnClickListener(v ->
                Toast.makeText(this, "Viewing reviews for " + currentUserName, Toast.LENGTH_SHORT).show()
        );

        navBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadDashboardData();
    }

    private void setGreetingText(String username) {
        String prefix = "What's Tam-pting you today, ";
        String suffix = "?";
        String full = prefix + username + suffix;
        SpannableString spannable = new SpannableString(full);
        spannable.setSpan(
                new ForegroundColorSpan(Color.parseColor("#0A8A0A")),
                prefix.length(),
                prefix.length() + username.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        tvHomeGreeting.setText(spannable);
    }

    private void loadDashboardData() {
        allStalls = dbHelper.getAllStalls();
        allMenuItems = dbHelper.getAllMenuItems();
        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        String query = etSearchHome.getText() != null ? etSearchHome.getText().toString().trim().toLowerCase() : "";

        List<Stall> filteredStalls = new ArrayList<>();
        for (Stall s : allStalls) {
            if (onlyOpenStalls && s.getIsOpen() != 1) {
                continue;
            }
            String name = s.getStallName() != null ? s.getStallName().toLowerCase() : "";
            String loc = s.getLocation() != null ? s.getLocation().toLowerCase() : "";
            if (query.isEmpty() || name.contains(query) || loc.contains(query)) {
                filteredStalls.add(s);
            }
        }

        switch (currentSortMode) {
            case HIGHEST_REVIEWS:
                Collections.sort(filteredStalls, (a, b) -> {
                    int byCount = Integer.compare(b.getReviewCount(), a.getReviewCount());
                    if (byCount != 0) return byCount;
                    return Double.compare(b.getAverageRating(), a.getAverageRating());
                });
                break;
            case LOWEST_REVIEWS:
                Collections.sort(filteredStalls, (a, b) -> {
                    int byCount = Integer.compare(a.getReviewCount(), b.getReviewCount());
                    if (byCount != 0) return byCount;
                    return Double.compare(a.getAverageRating(), b.getAverageRating());
                });
                break;
            case A_TO_Z:
                Collections.sort(filteredStalls, (a, b) -> {
                    String nameA = a.getStallName() != null ? a.getStallName() : "";
                    String nameB = b.getStallName() != null ? b.getStallName() : "";
                    return nameA.compareToIgnoreCase(nameB);
                });
                break;
            case Z_TO_A:
                Collections.sort(filteredStalls, (a, b) -> {
                    String nameA = a.getStallName() != null ? a.getStallName() : "";
                    String nameB = b.getStallName() != null ? b.getStallName() : "";
                    return nameB.compareToIgnoreCase(nameA);
                });
                break;
            case DEFAULT:
            default:
                break;
        }

        List<MenuItem> filteredMenu = new ArrayList<>();
        for (MenuItem m : allMenuItems) {
            String food = m.getFoodName() != null ? m.getFoodName().toLowerCase() : "";
            String stall = m.getStallName() != null ? m.getStallName().toLowerCase() : "";
            if (query.isEmpty() || food.contains(query) || stall.contains(query)) {
                filteredMenu.add(m);
            }
        }

        stallAdapter.updateList(filteredStalls);
        recommendationAdapter.updateList(filteredMenu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }
    private enum SortMode {
        DEFAULT("⇅ Sort"),
        HIGHEST_REVIEWS("⇅ Highest Reviews"),
        LOWEST_REVIEWS("⇅ Lowest Reviews"),
        A_TO_Z("⇅ A–Z"),
        Z_TO_A("⇅ Z–A");

        final String label;

        SortMode(String label) {
            this.label = label;
        }
    }

    private SortMode currentSortMode = SortMode.DEFAULT;
}