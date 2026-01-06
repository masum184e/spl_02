package com.example.odyssey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.LoginRequest;
import com.example.odyssey.models.LoginResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity {

    private TextView signupLink;
    private Button loginButton;
    private SharedPreferences sharedPreferences;
    private TextInputLayout getEmail, getPassword;
    private TextInputEditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("authToken")) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_signin);

        loginButton=findViewById(R.id.login_button);
        signupLink=findViewById(R.id.signup_link);
        getEmail = findViewById(R.id.get_email);
        getPassword = findViewById(R.id.get_password);

        emailEditText = (TextInputEditText) getEmail.getEditText();
        passwordEditText = (TextInputEditText) getPassword.getEditText();

        signupLink.setOnClickListener(view -> startActivity(new Intent(this, SignupActivity.class)));

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            getEmail.setError(email.isEmpty() ? "Email is required" : null);
            getPassword.setError(password.isEmpty() ? "Password is required" : null);

            if (!email.isEmpty() && !password.isEmpty()) loginUser(email, password);
        });

    }

    private void loginUser(String email, String password) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<LoginResponse> call = apiService.loginUser(new LoginRequest(email, password));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Toast.makeText(SigninActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    if ("true".equals(loginResponse.isStatus())) {
                        getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE).edit().putString("authToken", loginResponse.getToken()).apply();
                        getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE).edit().putString("userRole", loginResponse.getRole()).apply();
                        startActivity(new Intent(SigninActivity.this, HomeActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(SigninActivity.this, "Failed to log in", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(SigninActivity.this, "Network error occurred", Toast.LENGTH_LONG).show();
            }
        });

    }
}
