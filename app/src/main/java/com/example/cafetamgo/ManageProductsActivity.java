package com.example.cafetamgo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity {

    private RecyclerView rvManageProducts;
    private EditText etSearchProducts;
    private Button btnAddProduct;
    private DatabaseHelper dbHelper;
    private ManageProductAdapter adapter;
    private List<MenuItem> baseProducts = new ArrayList<>();
    private int filterStallId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        dbHelper = new DatabaseHelper(this);
        rvManageProducts = findViewById(R.id.rvManageProducts);
        etSearchProducts = findViewById(R.id.etSearchProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        filterStallId = getIntent().getIntExtra("STALL_ID", -1);

        rvManageProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageProductAdapter(new ArrayList<>(), item -> {
            Intent intent = new Intent(ManageProductsActivity.this, AddEditProductActivity.class);
            intent.putExtra("MENU_ITEM_ID", item.getMenuItemId());
            intent.putExtra("STALL_ID", item.getStallId());
            startActivity(intent);
        });
        rvManageProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ManageProductsActivity.this, AddEditProductActivity.class);
            int targetStallId = (filterStallId != -1) ? filterStallId : getDefaultStallId();
            intent.putExtra("STALL_ID", targetStallId);
            startActivity(intent);
        });

        etSearchProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        loadProducts();
    }

    private int getDefaultStallId() {
        List<Stall> stalls = dbHelper.getAllStalls();
        return stalls.isEmpty() ? 1 : stalls.get(0).getStallId();
    }

    private void loadProducts() {
        List<MenuItem> allItems = dbHelper.getAllMenuItems();
        baseProducts.clear();

        for (MenuItem item : allItems) {
            if (filterStallId == -1 || item.getStallId() == filterStallId) {
                baseProducts.add(item);
            }
        }

        applyFilter(etSearchProducts.getText().toString());
    }

    private void applyFilter(String query) {
        String lower = query == null ? "" : query.trim().toLowerCase();
        if (lower.isEmpty()) {
            adapter.updateList(new ArrayList<>(baseProducts));
            return;
        }

        List<MenuItem> filtered = new ArrayList<>();
        for (MenuItem item : baseProducts) {
            String name = item.getFoodName() != null ? item.getFoodName().toLowerCase() : "";
            String category = item.getCategory() != null ? item.getCategory().toLowerCase() : "";
            if (name.contains(lower) || category.contains(lower)) {
                filtered.add(item);
            }
        }
        adapter.updateList(filtered);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}