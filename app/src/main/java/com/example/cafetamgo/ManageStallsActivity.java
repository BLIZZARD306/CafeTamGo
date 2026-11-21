package com.example.cafetamgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ManageStallsActivity extends AppCompatActivity {

    private RecyclerView rvManageStalls;
    private Button btnAddStall;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stalls);

        dbHelper = new DatabaseHelper(this);
        rvManageStalls = findViewById(R.id.rvManageStalls);
        btnAddStall = findViewById(R.id.btnAddStall);

        rvManageStalls.setLayoutManager(new LinearLayoutManager(this));

        loadStalls();

        btnAddStall.setOnClickListener(v -> {
            Intent intent = new Intent(ManageStallsActivity.this, AddEditStallActivity.class);
            startActivity(intent);
        });
    }

    private void loadStalls() {
        List<Stall> stallList = dbHelper.getAllStalls();
        ManageStallAdapter adapter = new ManageStallAdapter(stallList, new ManageStallAdapter.StallActionListener() {
            @Override
            public void onEditStall(Stall stall) {
                Intent intent = new Intent(ManageStallsActivity.this, AddEditStallActivity.class);
                intent.putExtra("STALL_ID", stall.getStallId());
                startActivity(intent);
            }

            @Override
            public void onViewProducts(Stall stall) {
                Intent intent = new Intent(ManageStallsActivity.this, ManageProductsActivity.class);
                intent.putExtra("STALL_ID", stall.getStallId());
                startActivity(intent);
            }
        });
        rvManageStalls.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStalls();
    }
}