package com.example.odyssey;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.ProfileResponse;
import com.example.odyssey.models.UnavailableDateListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilityCalendar extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private SharedPreferences sharedPreferences;
    private String bearerToken;
    private LinearLayout dateListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability_calendar);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Unavailable Dates");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        bearerToken = sharedPreferences.getString("authToken", null);

        dateListLayout = findViewById(R.id.date_list_layout);
        fetchUnavailableDateList();

    }

    private void fetchUnavailableDateList() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<UnavailableDateListResponse> call = apiService.getUnavailableDates("Bearer " + bearerToken);

        call.enqueue(new Callback<UnavailableDateListResponse>() {
            @Override
            public void onResponse(Call<UnavailableDateListResponse> call, Response<UnavailableDateListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UnavailableDateListResponse unavailableDateListResponse = response.body();
                    System.out.println(response.body().getDateList());
                    System.out.println(response.body().getMessage());
                    System.out.println(response.body().getStatus());
                    if (unavailableDateListResponse != null
                            && unavailableDateListResponse.getDateList() != null) {
                        dateListLayout.removeAllViews();

                        for (String date : unavailableDateListResponse.getDateList()) {
                            TextView dateTextView = new TextView(AvailabilityCalendar.this);
                            dateTextView.setText(date);
                            dateTextView.setTextSize(18);
                            dateTextView.setPadding(16, 16, 16, 16);
                            dateListLayout.addView(dateTextView);
                        }
                    } else {
                        Toast.makeText(AvailabilityCalendar.this,
                                "No dates available or date list is null",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AvailabilityCalendar.this,
                            "Response unsuccessful: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UnavailableDateListResponse> call, Throwable t) {
                Toast.makeText(AvailabilityCalendar.this, "Failed fetch dates", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}