package com.example.cafetamgo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin, tvRegisterBrand;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        tvRegisterBrand = findViewById(R.id.tvRegisterBrand);
        etName = findViewById(R.id.etRegisterName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        styleBrandText(tvRegisterBrand);
        styleFooterLink(tvGoToLogin, "Already have an account? ", "Login here.");

        btnRegister.setOnClickListener(v -> handleRegister());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void styleBrandText(TextView target) {
        String full = "CafeTamGo!";
        SpannableString span = new SpannableString(full);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#0A8A0A")), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#C68B00")), 7, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        target.setText(span);
    }

    private void styleFooterLink(TextView target, String prefix, String action) {
        String full = prefix + action;
        SpannableString span = new SpannableString(full);
        int start = prefix.length();
        int end = full.length();
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#0A8A0A")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        target.setText(span);
    }

    private void showMessage(String message) {
        runOnUiThread(() -> Toast.makeText(
                RegisterActivity.this,
                message,
                Toast.LENGTH_LONG
        ).show());
    }

    private void handleRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty()) {
            showMessage("Please enter your name");
            return;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            showMessage("Name must not contain special characters or numbers");
            return;
        }

        if (email.isEmpty()) {
            showMessage("Please enter your email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage("Please enter a valid email address");
            return;
        }

        if (password.isEmpty()) {
            showMessage("Please enter your password");
            return;
        }

        if (password.length() < 8) {
            showMessage("Password must be at least 8 characters long");
            return;
        }

        User newUser = new User(0, name, email, password, "CUSTOMER");
        boolean created = dbHelper.addUser(newUser);
        if (created) {
            showMessage("Account registered! Please log in.");
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } else {
            showMessage("Registration failed. Email may already be in use.");
        }
    }
}