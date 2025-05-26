package com.example.karyaku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import com.example.karyaku.utils.DBHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword;
    Button btnRegister, btnToLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnToLogin = findViewById(R.id.btnToLogin);

        btnRegister.setOnClickListener(view -> {
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = dbHelper.registerUser(username, email, password);
            if (inserted) {
                Toast.makeText(this, "Registrasi berhasil, silakan login", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Email sudah digunakan", Toast.LENGTH_SHORT).show();
            }
        });

        btnToLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
