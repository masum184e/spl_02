package com.example.odyssey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.RegistrationRequest;
import com.example.odyssey.models.RegistrationResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private TextView signinLink;
    private Button signupBtn;
    private SharedPreferences sharedPreferences;
    private Spinner roleSpinner;
    private TextInputLayout getFullName, getEmail,getMobileNumber, getPassword;
    private TextInputEditText fullNameEditText,emailEditText,mobileNumberEditText, passwordEditText;
    private String selectedRole;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("authToken")) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_signup);

        getFullName = findViewById(R.id.get_full_name);
        getEmail = findViewById(R.id.get_email);
        getMobileNumber = findViewById(R.id.get_mobile_number);
        getPassword = findViewById(R.id.get_password);
        roleSpinner = findViewById(R.id.role_spinner);
        signupBtn = findViewById(R.id.signup_btn);
        signinLink = findViewById(R.id.signin_link);

        fullNameEditText = (TextInputEditText) getFullName.getEditText();
        emailEditText = (TextInputEditText) getEmail.getEditText();
        mobileNumberEditText = (TextInputEditText) getMobileNumber.getEditText();
        passwordEditText = (TextInputEditText) getPassword.getEditText();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signinLink.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, SigninActivity.class)));
        
        signupBtn.setOnClickListener(view -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String mobileNumber = mobileNumberEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            getFullName.setError(email.isEmpty() ? "Name is required" : null);
            getEmail.setError(email.isEmpty() ? "Email is required" : null);
            getMobileNumber.setError(email.isEmpty() ? "Mobile Number is required" : null);
            getPassword.setError(password.isEmpty() ? "Password is required" : null);

            if (!fullName.isEmpty() && !email.isEmpty() && !mobileNumber.isEmpty() && !password.isEmpty() && !selectedRole.isEmpty()) registerUser(fullName, email, mobileNumber, selectedRole, password);
        });

    }

    private void registerUser(String fullName, String email, String mobileNumber, String role, String password){
        ApiService apiService = RetrofitClient.getApiService();
        Call<RegistrationResponse> call = apiService.userRegistration(new RegistrationRequest(fullName, email, mobileNumber, role, password));

        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegistrationResponse registrationResponse = response.body();
                    Toast.makeText(SignupActivity.this, registrationResponse.getMessage(), Toast.LENGTH_LONG).show();
                    if ("true".equals(registrationResponse.isStatus())) {
                        getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE).edit().putString("authToken", registrationResponse.getToken()).apply();
                        getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE).edit().putString("userRole", registrationResponse.getRole()).apply();
                        startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Failed to Registration", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Network error occurred "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}