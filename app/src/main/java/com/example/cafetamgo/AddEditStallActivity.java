package com.example.cafetamgo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEditStallActivity extends AppCompatActivity {

    private static final Pattern STRICT_TIME_PATTERN =
            Pattern.compile("^(0[1-9]|1[0-2]):([0-5][0-9]) (AM|PM)$");
    private static final int MIN_ALLOWED_MINUTES = 6 * 60;  // 06:00 AM
    private static final int MAX_ALLOWED_MINUTES = 21 * 60; // 09:00 PM

    private TextView tvHeaderTitle;
    private EditText etStallName, etStallLocation, etStallDescription, etOpeningTime, etClosingTime;
    private Switch switchIsOpen;
    private Button btnSaveStall, btnCancelStall;
    private DatabaseHelper dbHelper;
    private int stallId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_stall);

        dbHelper = new DatabaseHelper(this);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        etStallName = findViewById(R.id.etStallName);
        etStallLocation = findViewById(R.id.etStallLocation);
        etStallDescription = findViewById(R.id.etStallDescription);
        etOpeningTime = findViewById(R.id.etOpeningTime);
        etClosingTime = findViewById(R.id.etClosingTime);
        switchIsOpen = findViewById(R.id.switchIsOpen);
        btnSaveStall = findViewById(R.id.btnSaveStall);
        btnCancelStall = findViewById(R.id.btnCancelStall);

        stallId = getIntent().getIntExtra("STALL_ID", -1);

        if (stallId != -1) {
            tvHeaderTitle.setText("Edit Stall");
            btnSaveStall.setText("Edit Stall");
            loadStallData(stallId);
        }

        btnSaveStall.setOnClickListener(v -> saveStall());
        btnCancelStall.setOnClickListener(v -> finish());
    }

    private void showMessage(String message) {
        runOnUiThread(() -> Toast.makeText(
                AddEditStallActivity.this,
                message,
                Toast.LENGTH_LONG
        ).show());
    }

    private Integer parseStrictMinutesInWindow(String time) {
        if (time == null) return null;
        Matcher matcher = STRICT_TIME_PATTERN.matcher(time);
        if (!matcher.matches()) return null;

        int hour = Integer.parseInt(matcher.group(1));
        int minute = Integer.parseInt(matcher.group(2));
        String period = matcher.group(3).toUpperCase(Locale.ROOT);

        int hour24;
        if ("AM".equals(period)) {
            hour24 = (hour == 12) ? 0 : hour;
        } else {
            hour24 = (hour == 12) ? 12 : hour + 12;
        }

        int totalMinutes = hour24 * 60 + minute;
        if (totalMinutes < MIN_ALLOWED_MINUTES || totalMinutes > MAX_ALLOWED_MINUTES) {
            return null;
        }
        return totalMinutes;
    }

    private void loadStallData(int id) {
        for (Stall s : dbHelper.getAllStalls()) {
            if (s.getStallId() == id) {
                etStallName.setText(s.getStallName());
                etStallLocation.setText(s.getLocation());
                etStallDescription.setText(s.getDescription());
                etOpeningTime.setText(s.getOpeningTime());
                etClosingTime.setText(s.getClosingTime());
                switchIsOpen.setChecked(s.getIsOpen() == 1);
                break;
            }
        }
    }

    private void saveStall() {
        String name = etStallName.getText().toString().trim();
        String location = etStallLocation.getText().toString().trim();
        String desc = etStallDescription.getText().toString().trim();
        String open = etOpeningTime.getText().toString().trim();
        String close = etClosingTime.getText().toString().trim();
        int isOpen = switchIsOpen.isChecked() ? 1 : 0;

        if (name.isEmpty()) {
            showMessage("Please enter a stall name");
            return;
        }

        if (!name.matches("[a-zA-Z0-9 ]+")) {
            showMessage("Stall name must not contain special characters");
            return;
        }

        if (location.isEmpty()) {
            showMessage("Please enter a stall location");
            return;
        }

        Integer openMinutes = parseStrictMinutesInWindow(open);
        if (openMinutes == null) {
            showMessage("Opening time must be in 00:00 AM or 00:00 PM format between 06:00 AM and 09:00 PM");
            return;
        }

        Integer closeMinutes = parseStrictMinutesInWindow(close);
        if (closeMinutes == null) {
            showMessage("Closing time must be in 00:00 AM or 00:00 PM format between 06:00 AM and 09:00 PM");
            return;
        }

        if (closeMinutes <= openMinutes) {
            showMessage("Closing time must be later than opening time");
            return;
        }

        boolean success;
        if (stallId == -1) {
            success = dbHelper.addStall(name, location, open, close, desc, isOpen);
        } else {
            success = dbHelper.updateStall(stallId, name, location, open, close, desc, isOpen);
        }

        if (success) {
            showMessage("Stall saved successfully!");
            finish();
        } else {
            showMessage("Error saving stall");
        }
    }
}