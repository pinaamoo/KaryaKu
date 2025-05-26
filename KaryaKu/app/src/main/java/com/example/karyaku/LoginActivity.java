package com.example.karyaku;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import com.example.karyaku.utils.DBHelper;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin;
    DBHelper dbHelper;
    Button btnToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnToRegister = findViewById(R.id.btnToRegister);

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = dbHelper.loginUser(email, password);
            if (cursor.moveToFirst()) {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));

                SharedPreferences.Editor editor = getSharedPreferences("session", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putInt("user_id", userId);
                editor.putString("username", username);
                editor.apply();

                Toast.makeText(this, "Login berhasil, selamat datang " + username, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Login gagal. Periksa email & password", Toast.LENGTH_SHORT).show();
            }
        });

        btnToRegister.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }
}
