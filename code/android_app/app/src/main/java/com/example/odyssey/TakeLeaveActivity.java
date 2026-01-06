package com.example.odyssey;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.ApiResponse;
import com.example.odyssey.models.ProfileResponse;
import com.example.odyssey.models.TakeLeaveRequest;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.odyssey.databinding.ActivityTakeLeaveBinding;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakeLeaveActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private LinearLayout startDateContainer;
    private LinearLayout endDateContainer;
    private TextView startDate;
    private TextView endDate;
    private Button leaveBtn;
    private String bearerToken;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_leave);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Take Leave");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        bearerToken = sharedPreferences.getString("authToken", null);
        if (bearerToken == null) {
            Toast.makeText(this, "Unauthorized User", Toast.LENGTH_LONG).show();
            logout();
        }

        startDateContainer = findViewById(R.id.start_date_container);
        endDateContainer = findViewById(R.id.end_date_container);
        startDate = findViewById(R.id.start_datepicker_hint);
        endDate = findViewById(R.id.end_datepicker_hint);
        leaveBtn = findViewById(R.id.leave_btn);

        startDateContainer.setOnClickListener(v -> showDatePicker(startDate));
        endDateContainer.setOnClickListener(v -> showDatePicker(endDate));
        leaveBtn.setOnClickListener(v -> {
            String startDateStr = startDate.getText().toString().trim();
            String endDateStr = endDate.getText().toString().trim();

            if (startDateStr.isEmpty() || startDateStr.equals("Select Date")) {
                Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (endDateStr.isEmpty() || endDateStr.equals("Select Date")) {
                Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String formattedStartDate = convertToSqlDateTime(startDateStr);
                String formattedEndDate = convertToSqlDateTime(endDateStr);

                LocalDate leaveStartDate = LocalDate.parse(formattedStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate leaveEndDate = LocalDate.parse(formattedEndDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                if (leaveStartDate.isBefore(LocalDate.now())) {
                    Toast.makeText(this, "Start date cannot be in the past", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (leaveStartDate.isAfter(leaveEndDate)) {
                    Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendLeaveRequest(formattedStartDate, formattedEndDate);
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendLeaveRequest(String startDate, String endDate) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiResponse> call = apiService.makeLeaveRequest("Bearer " + bearerToken, new TakeLeaveRequest(startDate, endDate));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(TakeLeaveActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TakeLeaveActivity.this, "Failed to request leave", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(TakeLeaveActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public String convertToSqlDateTime(String date) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(date, inputFormatter);
        return parsedDate.format(sqlFormatter);
    }

    private void logout() {
        sharedPreferences.edit().remove("authToken").apply();
        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, SigninActivity.class));
        finish();
    }

    private void showDatePicker(TextView field) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) ->
                        field.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

}