package com.example.cafetamgo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditProductActivity extends AppCompatActivity {

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

        if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Name, category, and price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int priceInCentavos = (int) Math.round(Double.parseDouble(priceStr) * 100.0);

        boolean success;
        if (menuItemId == -1) {
            success = dbHelper.addMenuItem(stallId, name, desc, category, priceInCentavos, isAvailable);
        } else {
            success = dbHelper.updateMenuItem(menuItemId, name, desc, category, priceInCentavos, isAvailable);
        }

        if (success) {
            Toast.makeText(this, "Product saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeProduct() {
        if (menuItemId != -1 && dbHelper.deleteMenuItem(menuItemId)) {
            Toast.makeText(this, "Product removed", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to remove product", Toast.LENGTH_SHORT).show();
        }
    }
}