package com.example.cafetamgo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class AddEditProductActivity extends AppCompatActivity {

    private static final List<String> ALLOWED_CATEGORIES =
            Arrays.asList("Rice Meals", "Pasta", "Drinks");

    private TextView tvProductHeaderTitle;
    private EditText etProductName, etProductDescription, etProductCategory, etProductPrice;
    private Switch switchIsAvailable;
    private Button btnSaveProduct, btnRemoveProduct;
    private DatabaseHelper dbHelper;
    private int stallId = -1;
    private int menuItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        dbHelper = new DatabaseHelper(this);
        tvProductHeaderTitle = findViewById(R.id.tvProductHeaderTitle);
        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etProductCategory = findViewById(R.id.etProductCategory);
        etProductPrice = findViewById(R.id.etProductPrice);
        switchIsAvailable = findViewById(R.id.switchIsAvailable);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        btnRemoveProduct = findViewById(R.id.btnRemoveProduct);

        stallId = getIntent().getIntExtra("STALL_ID", 1);
        menuItemId = getIntent().getIntExtra("MENU_ITEM_ID", -1);

        if (menuItemId != -1) {
            tvProductHeaderTitle.setText("Edit Product");
            btnSaveProduct.setText("Save Product");
            btnRemoveProduct.setVisibility(View.VISIBLE);
            loadProductData(menuItemId);
        } else {
            btnRemoveProduct.setVisibility(View.GONE);
        }

        btnSaveProduct.setOnClickListener(v -> saveProduct());
        btnRemoveProduct.setOnClickListener(v -> removeProduct());
    }

    private void showMessage(String message) {
        runOnUiThread(() -> Toast.makeText(
                AddEditProductActivity.this,
                message,
                Toast.LENGTH_LONG
        ).show());
    }

    private void loadProductData(int id) {
        for (MenuItem item : dbHelper.getAllMenuItems()) {
            if (item.getMenuItemId() == id) {
                stallId = item.getStallId();
                etProductName.setText(item.getFoodName());
                etProductDescription.setText(item.getDescription());
                etProductCategory.setText(item.getCategory());
                etProductPrice.setText(String.format("%.2f", item.getPrice() / 100.0));
                switchIsAvailable.setChecked(item.getIsAvailable() == 1);
                break;
            }
        }
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String desc = etProductDescription.getText().toString().trim();
        String category = etProductCategory.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        int isAvailable = switchIsAvailable.isChecked() ? 1 : 0;

        if (name.isEmpty()) {
            showMessage("Please enter a product name");
            return;
        }

        if (!name.matches("[a-zA-Z0-9 ]+")) {
            showMessage("Product name must only contain letters, numbers, and spaces");
            return;
        }

        if (category.isEmpty()) {
            showMessage("Please enter a category");
            return;
        }

        if (!ALLOWED_CATEGORIES.contains(category)) {
            showMessage("Category must be Rice Meals, Pasta, or Drinks");
            return;
        }

        if (priceStr.isEmpty()) {
            showMessage("Please enter a price");
            return;
        }

        double priceValue;
        try {
            priceValue = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid price");
            return;
        }

        if (priceValue <= 0) {
            showMessage("Price must be greater than 0");
            return;
        }

        int priceInCentavos = (int) Math.round(priceValue * 100.0);

        boolean success;
        if (menuItemId == -1) {
            success = dbHelper.addMenuItem(stallId, name, desc, category, priceInCentavos, isAvailable);
        } else {
            success = dbHelper.updateMenuItem(menuItemId, name, desc, category, priceInCentavos, isAvailable);
        }

        if (success) {
            showMessage("Product saved!");
            finish();
        } else {
            showMessage("Failed to save product");
        }
    }

    private void removeProduct() {
        if (menuItemId != -1 && dbHelper.deleteMenuItem(menuItemId)) {
            showMessage("Product removed");
            finish();
        } else {
            showMessage("Failed to remove product");
        }
    }
}