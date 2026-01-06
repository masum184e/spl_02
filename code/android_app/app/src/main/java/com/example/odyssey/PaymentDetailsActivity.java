package com.example.odyssey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.AmountRequest;
import com.example.odyssey.models.AmountResponse;
import com.example.odyssey.models.ApiResponse;
import com.example.odyssey.models.BookingRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDetailsActivity extends AppCompatActivity {

    private TextView pickupDateTimeValue;
    private TextView dropoffDateTimeValue;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private Button sendReqBtn;
    private Button cancelBtn;
    private String bearerToken;
    private SharedPreferences sharedPreferences;
    private TextView totalDuration;
    private TextView totalDistance;
    private TextView totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Payment Details");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        bearerToken = sharedPreferences.getString("authToken", null);
        if (bearerToken == null || bearerToken.isEmpty()) {
            Toast.makeText(this, "Unauthorized User", Toast.LENGTH_LONG).show();
            logout();
        }

        pickupDateTimeValue = findViewById(R.id.pickup_date_time_value);
        dropoffDateTimeValue = findViewById(R.id.dropoff_date_time_value);

        sendReqBtn = findViewById(R.id.send_req_btn);
        cancelBtn = findViewById(R.id.cancel_button);

        totalDuration = findViewById(R.id.total_duration_value);
        totalDistance = findViewById(R.id.total_distance_value);
        totalAmount = findViewById(R.id.total_amount_value);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String driverId = extras.getString("DRIVER_ID", "N/A");
                String pickupDatetime = extras.getString("PICKUP_DATETIME", "N/A");
                String dropoffDatetime = extras.getString("DROPOFF_DATETIME", "N/A");
                String pickupLocation = extras.getString("PICKUP_LOCATION", "N/A");
                String dropoffLocation = extras.getString("DROPOFF_LOCATION", "N/A");
                String numberOfPassengers = extras.getString("NUMBER_OF_PASSENGER", "0");
                String numberOfStoppages = extras.getString("NUMBER_OF_STOPPAGE", "0");

                setAmountDetails(new AmountRequest(driverId, pickupDatetime, dropoffDatetime, pickupLocation, dropoffLocation));

                pickupDateTimeValue.setText(convertToDisplayFormat(pickupDatetime));
                dropoffDateTimeValue.setText(convertToDisplayFormat(dropoffDatetime));

                BookingRequest bookingRequest = new BookingRequest(
                        driverId,
                        pickupDatetime,
                        dropoffDatetime,
                        pickupLocation,
                        dropoffLocation,
                        numberOfPassengers,
                        numberOfStoppages
                );
                sendReqBtn.setOnClickListener(v -> {
                    sendBookingRequest(bookingRequest);
                });
            } else {
                Toast.makeText(this, "No booking details received.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No intent data received.", Toast.LENGTH_SHORT).show();
            finish();
        }

        cancelBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setAmountDetails(AmountRequest amountRequest) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<AmountResponse> call = apiService.checkAmount("Bearer " + bearerToken, amountRequest);

        call.enqueue(new Callback<AmountResponse>() {
            @Override
            public void onResponse(Call<AmountResponse> call, Response<AmountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PaymentDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    totalDuration.setText(response.body().getDuration() + "Hour");
                    totalDistance.setText(response.body().getDistance() + "KM");
                    totalAmount.setText(response.body().getTotal_price() + "৳");
                } else {
                    Toast.makeText(PaymentDetailsActivity.this, "Failed to retrieve payment amount", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AmountResponse> call, Throwable t) {
                Toast.makeText(PaymentDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendBookingRequest(BookingRequest bookingRequest) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiResponse> call = apiService.createBooking("Bearer " + bearerToken, bookingRequest);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Toast.makeText(PaymentDetailsActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String rawError = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("ServerError", "Raw Response: " + rawError);
                    } catch (Exception e) {
                        Log.e("ServerError", "Error reading error body", e);
                    }
                    Toast.makeText(PaymentDetailsActivity.this, "Failed to create booking", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(PaymentDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    private void logout() {
        sharedPreferences.edit().remove("authToken").apply();
        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, SigninActivity.class));
        finish();
    }

    public String convertToDisplayFormat(String datetime) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
            LocalDateTime dateTime = LocalDateTime.parse(datetime, inputFormatter);
            return dateTime.format(displayFormatter);
        } catch (Exception e) {
            Log.e("DateConversionError", "Invalid date format: " + datetime, e);
            return "Invalid Date";
        }
    }
}
