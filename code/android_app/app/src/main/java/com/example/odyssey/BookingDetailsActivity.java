package com.example.odyssey;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.BookingRequest;
import com.example.odyssey.models.BookingResponse;
import com.example.odyssey.models.ProfileResponse;
import com.example.odyssey.models.VehicleResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView carImage;
    private TextView carModel;
    private TextView carRating;
    private LinearLayout pickupDateContainer;
    private LinearLayout pickupTimeContainer;
    private LinearLayout dropoffDateContainer;
    private LinearLayout dropoffTimeContainer;
    private TextView pickupDate;
    private TextView pickupTime;
    private TextView dropoffDate;
    private TextView dropoffTime;
    private GoogleMap pickupMap;
    private GoogleMap dropoffMap;
    private String bearerToken;
    private SharedPreferences sharedPreferences;
    private Button bookingReqstBtn;
    private Button cancelButton;
    private TextView driverName;
    private TextView driverMobile;
    private TextView renterName;
    private TextView renterMobile;
    private int driverId;
    private TextInputLayout getNumOfPassenger, getNumOfStoppage;
    private TextInputEditText numOfPassengerEditText, numOfStoppageEditText;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Booking Details");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        carImage = findViewById(R.id.car_image);
        carModel = findViewById(R.id.car_model);
        carRating = findViewById(R.id.car_rating);
        driverName = findViewById(R.id.driver_name);
        driverMobile = findViewById(R.id.driver_mobile);
        renterName = findViewById(R.id.renter_name);
        renterMobile = findViewById(R.id.renter_mobile);

        pickupDateContainer = findViewById(R.id.pickup_date_container);
        pickupTimeContainer = findViewById(R.id.pickup_time_container);
        dropoffDateContainer = findViewById(R.id.dropoff_date_container);
        dropoffTimeContainer = findViewById(R.id.dropoff_time_container);

        pickupDate = findViewById(R.id.pickup_datepicker_hint);
        pickupTime = findViewById(R.id.pickup_timepicker_hint);
        dropoffDate = findViewById(R.id.dropoff_datepicker_hint);
        dropoffTime = findViewById(R.id.dropoff_timepicker_hint);

        bookingReqstBtn = findViewById(R.id.advance_payment_button);
        cancelButton = findViewById(R.id.cancel_button);

        getNumOfPassenger = findViewById(R.id.get_num_of_passenger);
        getNumOfStoppage = findViewById(R.id.get_num_of_stoppage);

        numOfPassengerEditText = (TextInputEditText) getNumOfPassenger.getEditText();
        numOfStoppageEditText = (TextInputEditText) getNumOfStoppage.getEditText();

        sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        bearerToken = sharedPreferences.getString("authToken", null);
        if (bearerToken == null) {
            Toast.makeText(this, "Unauthorized User", Toast.LENGTH_LONG).show();
            logout();
        } else {
            fetchUserProfile();
        }

        String carId = getIntent().getStringExtra("CAR_ID");
        if (carId != null) {
            try {
                int vehicleId = Integer.parseInt(carId);
                fetchVehicleById(vehicleId);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid vehicle ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Car ID not provided!", Toast.LENGTH_SHORT).show();
            finish();
        }

        pickupDateContainer.setOnClickListener(v -> showDatePicker(pickupDate));
        pickupTimeContainer.setOnClickListener(v -> showTimePicker(pickupTime));
        dropoffDateContainer.setOnClickListener(v -> showDatePicker(dropoffDate));
        dropoffTimeContainer.setOnClickListener(v -> showTimePicker(dropoffTime));

        SupportMapFragment pickupMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.pickup_map);
        SupportMapFragment dropoffMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.dropoff_map);

        if (pickupMapFragment != null) {
            pickupMapFragment.getMapAsync(googleMap -> {
                LatLng dhakaLocation = new LatLng(23.8103, 90.4125);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhakaLocation, 12));

                pickupMap = googleMap;
                setupMapClickListener(pickupMap, "Pickup");
            });
        }

        if (dropoffMapFragment != null) {
            dropoffMapFragment.getMapAsync(googleMap -> {
                LatLng dhakaLocation = new LatLng(23.8103, 90.4125);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhakaLocation, 12));

                dropoffMap = googleMap;
                setupMapClickListener(dropoffMap, "Dropoff");
            });
        }

        cancelButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        bookingReqstBtn.setOnClickListener(v -> {
            String pickupDateStr = pickupDate.getText().toString().trim();
            String pickupTimeStr = pickupTime.getText().toString().trim();
            String dropoffDateStr = dropoffDate.getText().toString().trim();
            String dropoffTimeStr = dropoffTime.getText().toString().trim();
            String pickupLocation = pickupMap != null ? pickupMap.getCameraPosition().target.toString() : "";
            String dropoffLocation = dropoffMap != null ? dropoffMap.getCameraPosition().target.toString() : "";
            String numOfPassengerStr = numOfPassengerEditText.getText().toString().trim();
            String numOfStoppageStr = numOfStoppageEditText.getText().toString().trim();

            boolean isValid = true;

            String pickupDatetime = pickupDateStr + " " + pickupTimeStr;
            String dropoffDatetime = dropoffDateStr + " " + dropoffTimeStr;
            String formattedPickupDatetime = "";
            String formattedDropoffDatetime = "";

            try {
                formattedPickupDatetime = convertToSqlDateTime(pickupDateStr, pickupTimeStr);
                LocalDateTime pickupDateTime = LocalDateTime.parse(formattedPickupDatetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                if (pickupDateTime.isBefore(LocalDateTime.now())) {
                    Toast.makeText(this, "Pickup date and time must be in the future.", Toast.LENGTH_LONG).show();
                    isValid = false;
                }
            } catch (DateTimeParseException e) {
                Toast.makeText(this, "Invalid pickup date or time format.", Toast.LENGTH_LONG).show();
                isValid = false;
            }

            try {
                formattedDropoffDatetime = convertToSqlDateTime(dropoffDateStr, dropoffTimeStr);
                LocalDateTime dropoffDateTime = LocalDateTime.parse(formattedDropoffDatetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                LocalDateTime pickupDateTime = LocalDateTime.parse(formattedPickupDatetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                if (!dropoffDateTime.isAfter(pickupDateTime)) {
                    Toast.makeText(this, "Dropoff date and time must be after pickup.", Toast.LENGTH_LONG).show();
                    isValid = false;
                }
            } catch (DateTimeParseException e) {
                Toast.makeText(this, "Invalid dropoff date or time format.", Toast.LENGTH_LONG).show();
                isValid = false;
            }

            if (pickupLocation.isEmpty() || pickupLocation.equals("lat/lng: (0.0,0.0)")) {
                Toast.makeText(this, "Please select a valid pickup location.", Toast.LENGTH_LONG).show();
                isValid = false;
            }

            if (dropoffLocation.isEmpty() || dropoffLocation.equals("lat/lng: (0.0,0.0)")) {
                Toast.makeText(this, "Please select a valid dropoff location.", Toast.LENGTH_LONG).show();
                isValid = false;
            }

            if (numOfPassengerStr.isEmpty()) {
                getNumOfPassenger.setError("Number of passengers is required.");
                isValid = false;
            } else {
                try {
                    int numOfPassenger = Integer.parseInt(numOfPassengerStr);
                    if (numOfPassenger <= 0) {
                        getNumOfPassenger.setError("Must be at least 1 passenger.");
                        isValid = false;
                    } else if (numOfPassenger > 10) {
                        getNumOfPassenger.setError("Maximum 10 passengers allowed.");
                        isValid = false;
                    } else {
                        getNumOfPassenger.setError(null);
                    }
                } catch (NumberFormatException e) {
                    getNumOfPassenger.setError("Enter a valid number.");
                    isValid = false;
                }
            }

            if (numOfStoppageStr.isEmpty()) {
                getNumOfStoppage.setError("Number of stoppages is required.");
                isValid = false;
            } else {
                try {
                    int numOfStoppage = Integer.parseInt(numOfStoppageStr);
                    if (numOfStoppage < 0) {
                        getNumOfStoppage.setError("Cannot be negative.");
                        isValid = false;
                    } else if (numOfStoppage > 5) {
                        getNumOfStoppage.setError("Maximum 5 stoppages allowed.");
                        isValid = false;
                    } else {
                        getNumOfStoppage.setError(null);
                    }
                } catch (NumberFormatException e) {
                    getNumOfStoppage.setError("Enter a valid number.");
                    isValid = false;
                }
            }

            if (isValid) {
                Intent intent = new Intent(v.getContext(), PaymentDetailsActivity.class);
                intent.putExtra("DRIVER_ID", String.valueOf(driverId));
                intent.putExtra("PICKUP_DATETIME", formattedPickupDatetime);
                intent.putExtra("DROPOFF_DATETIME", formattedDropoffDatetime);
                intent.putExtra("PICKUP_LOCATION", pickupLocation);
                intent.putExtra("DROPOFF_LOCATION", dropoffLocation);
                intent.putExtra("NUMBER_OF_PASSENGER", String.valueOf(numOfPassengerStr));
                intent.putExtra("NUMBER_OF_STOPPAGE", String.valueOf(numOfStoppageStr));
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    public String convertToSqlDateTime(String date, String time) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, inputFormatter);
        return dateTime.format(sqlFormatter);
    }

    private void fetchUserProfile() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ProfileResponse> call = apiService.getUserProfile("Bearer " + bearerToken);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profileResponse = response.body();
                    if ("true".equals(profileResponse.getStatus()) && profileResponse.getData() != null) {
                        renterName.setText(profileResponse.getData().getName());
                        renterMobile.setText(profileResponse.getData().getMobileNumber());
                    } else {
                        Toast.makeText(BookingDetailsActivity.this, profileResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(BookingDetailsActivity.this, "Failed to fetch profile", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(BookingDetailsActivity.this, "Network error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchVehicleById(int vehicleId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<VehicleResponse> call = apiService.getVehicleById(vehicleId);
        call.enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VehicleResponse vehicleResponse = response.body();

                    if ("true".equals(vehicleResponse.getStatus())) {
                        Glide.with(BookingDetailsActivity.this)
                                .load(vehicleResponse.getData().getMainImage())
                                .placeholder(R.drawable.car1)
                                .error(R.drawable.car1)
                                .into(carImage);

                        carModel.setText(vehicleResponse.getData().getModel());
                        driverId = vehicleResponse.getData().getDriverId();
                        driverName.setText(vehicleResponse.getData().getName());
                        driverMobile.setText(vehicleResponse.getData().getMobileNumber());

                    } else {
                        Toast.makeText(BookingDetailsActivity.this, vehicleResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BookingDetailsActivity.this, "Failed to fetch details: Invalid response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                Toast.makeText(BookingDetailsActivity.this, "Failed to fetch vehicle details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        sharedPreferences.edit().remove("authToken").apply();
        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, SigninActivity.class));
        finish();
    }

    private void setupMapClickListener(GoogleMap map, String mapType) {
        map.setOnMapClickListener(latLng -> {
            map.clear();
            if (isWithinBangladesh(latLng)) {
                String message = String.format(Locale.getDefault(), "%s Map Clicked: %.4f, %.4f", mapType, latLng.latitude, latLng.longitude);
                Toast.makeText(BookingDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                map.addMarker(new MarkerOptions().position(latLng).title(mapType + " Default Location"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            } else {
                Toast.makeText(BookingDetailsActivity.this, "Selection is outside Bangladesh.", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void showTimePicker(TextView field) {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(
                this,
                (view, hourOfDay, minute) ->
                        field.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
        ).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

    private boolean isWithinBangladesh(LatLng latLng) {
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        double minLatitude = 20.7433;
        double maxLatitude = 26.6234;
        double minLongitude = 88.0844;
        double maxLongitude = 92.6729;

        return latitude >= minLatitude && latitude <= maxLatitude &&
                longitude >= minLongitude && longitude <= maxLongitude;
    }
}