package com.example.cafetamgo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView btnGoToRegister, tvLoginBrand;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        tvLoginBrand = findViewById(R.id.tvLoginBrand);
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        styleBrandText(tvLoginBrand);
        styleFooterLink(btnGoToRegister, "Don't have an account? ", "Register here.");

        btnLogin.setOnClickListener(v -> handleLogin());

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
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
                LoginActivity.this,
                message,
                Toast.LENGTH_LONG
        ).show());
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

        User user = dbHelper.login(email, password);
        if (user != null) {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                intent.putExtra("USER_NAME", user.getName());
                intent.putExtra("USER_ID", user.getUserId());
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("USER_NAME", user.getName());
                intent.putExtra("USER_ID", user.getUserId());
                startActivity(intent);
            }
            finish();
        } else {
            showMessage("Invalid email or password");
        }
    }
}