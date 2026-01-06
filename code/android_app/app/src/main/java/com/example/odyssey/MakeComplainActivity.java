package com.example.odyssey;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.ComplaintsRequest;
import com.example.odyssey.models.ComplaintsResponse;
import com.example.odyssey.models.LoginRequest;
import com.example.odyssey.models.LoginResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MakeComplainActivity extends AppCompatActivity {


    private Button submitButton;
    private SharedPreferences sharedPreferences;
    private String bearerToken;
    private TextInputLayout getTitle, getDescription;
    private TextInputEditText titleEditText, descriptionEditText;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_complain);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Make Complain");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getTitle = findViewById(R.id.get_title);
        getDescription = findViewById(R.id.get_description);
        titleEditText = (TextInputEditText) getTitle.getEditText();
        descriptionEditText = (TextInputEditText) getDescription.getEditText();
        submitButton = findViewById(R.id.submit_button);

        sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        bearerToken = sharedPreferences.getString("authToken", null);

        submitButton.setOnClickListener(v -> validateAndSubmit());
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    private void validateAndSubmit() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        getTitle.setError(title.isEmpty() ? "Title is required" : null);
        getDescription.setError(description.isEmpty() ? "Description is required" : null);

        if (description.length() < 20) {
            getDescription.setError("Description must be at least 20 characters");
            getDescription.requestFocus();
            return;
        }

        submitComplaint(title, description);
    }


    private void submitComplaint(String title, String description) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ComplaintsResponse> call = apiService.makeComplaints("Bearer " + bearerToken, new ComplaintsRequest(title, description));

        call.enqueue(new Callback<ComplaintsResponse>() {

            @Override
            public void onResponse(Call<ComplaintsResponse> call, Response<ComplaintsResponse> response) {
                ComplaintsResponse complaintsResponse = response.body();
                if ("true".equals(complaintsResponse.getStatus()) && complaintsResponse.getMessage() != null) {
                    titleEditText.setText(null);
                    descriptionEditText.setText(null);

                    getTitle.clearFocus();
                    getDescription.clearFocus();

                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    }

                    Toast.makeText(MakeComplainActivity.this, complaintsResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MakeComplainActivity.this, complaintsResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ComplaintsResponse> call, Throwable t) {
                Toast.makeText(MakeComplainActivity.this, "Network error. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }
}