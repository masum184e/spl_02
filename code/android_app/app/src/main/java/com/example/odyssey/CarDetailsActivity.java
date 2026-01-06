package com.example.odyssey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.odyssey.adaptars.CarDetailsTabAdaptar;
import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.fragments.CarDetailsAboutFragment;
import com.example.odyssey.fragments.CarDetailsReviewFragment;
import com.example.odyssey.models.VehicleResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDetailsActivity extends AppCompatActivity {

    private ImageView horizontalImage;
    private ImageView mainCarImage;
    private LinearLayout imageContainer;
    private TextView carTitle;
    private Button bookNowButton;
    private TextView driverName;
    private TextView dirverMobile;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private CarDetailsTabAdaptar tabAdapter;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private SharedPreferences sharedPreferences;
    private String userRole;
    private String carDescription;
    private LinearLayout dateListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Vehicle Details");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mainCarImage = findViewById(R.id.main_car_image);
        carTitle = findViewById(R.id.car_title);
        viewPager = findViewById(R.id.view_pager_container);
        tabLayout = findViewById(R.id.tab_container);
        driverName = findViewById(R.id.driver_name);
        dirverMobile = findViewById(R.id.driver_mobile);
        imageContainer = findViewById(R.id.horizontal_images_container);
        dateListLayout = findViewById(R.id.date_list_layout);
        bookNowButton = findViewById(R.id.bottom_btn);

        String carId = getIntent().getStringExtra("CAR_ID");
        bookNowButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookingDetailsActivity.class);
            intent.putExtra("CAR_ID", carId);
            v.getContext().startActivity(intent);
        });

        userRole = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE).getString("userRole", null);

        if ("driver".equals(userRole)) {
            bookNowButton.setVisibility(View.GONE);
        }

        if (carId != null) {
            try {
                int vehicleId = Integer.parseInt(carId);
                fetchVehicleById(vehicleId);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid vehicle ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Car ID not provided!", Toast.LENGTH_SHORT).show();
            // finish();
        }


    }

    private void setupTabs() {
        tabAdapter = new CarDetailsTabAdaptar(this);
        Bundle bundle = new Bundle();
        bundle.putString("CAR_DESCRIPTION", carDescription);
        CarDetailsAboutFragment carDetailsAboutFragment = new CarDetailsAboutFragment();
        carDetailsAboutFragment.setArguments(bundle);

        tabAdapter.addFragment(carDetailsAboutFragment, "About");

        viewPager.setAdapter(tabAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(tabAdapter.getPageTitle(position))
        ).attach();
    }

    private void fetchVehicleById(int vehicleId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<VehicleResponse> call = apiService.getVehicleById(vehicleId);
        call.enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VehicleResponse vehicleResponse = response.body();

                    carDescription = "Introducing a " + vehicleResponse.getData().getModel() + " vehicle that combines sophistication and reliability. Here are the details:" +
                            "\n- Type: " + vehicleResponse.getData().getType() +
                            "\n- License Plate Number: " + vehicleResponse.getData().getLicensePlateNumber() +
                            "\n- Mileage: " + vehicleResponse.getData().getMileage() + "miles" +
                            "\n- Number of Seats: " + vehicleResponse.getData().getNumberOfSeats() +
                            "\n- Chassis Number: : " + vehicleResponse.getData().getChasisNumber() +
                            "\n- Year: " + vehicleResponse.getData().getYear() +
                            "\n- Color: " + vehicleResponse.getData().getColor();

                    if ("true".equals(vehicleResponse.getStatus())) {
                        Glide.with(CarDetailsActivity.this)
                                .load(vehicleResponse.getData().getMainImage())
                                .placeholder(R.drawable.car1)
                                .error(R.drawable.car1)
                                .into(mainCarImage);

                        setupImageSlider(
                                vehicleResponse.getData().getMainImage(),
                                vehicleResponse.getData().getFrontImage(),
                                vehicleResponse.getData().getBackImage(),
                                vehicleResponse.getData().getLeftImage(),
                                vehicleResponse.getData().getRightImage(),
                                vehicleResponse.getData().getInteriorImage()
                        );

                        carTitle.setText(vehicleResponse.getData().getModel());
                        driverName.setText(vehicleResponse.getData().getName());
                        dirverMobile.setText(vehicleResponse.getData().getMobileNumber());
                        setupTabs();

                        dateListLayout.removeAllViews();
                        for (String date : vehicleResponse.getData().getDates()) {
                            TextView dateTextView = new TextView(CarDetailsActivity.this);
                            dateTextView.setText(date);
                            dateTextView.setTextSize(18);
                            dateTextView.setPadding(16, 16, 16, 16);
                            dateListLayout.addView(dateTextView);
                        }

                    } else {
                        Toast.makeText(CarDetailsActivity.this, vehicleResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CarDetailsActivity.this, "Failed to fetch details: Invalid response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                Toast.makeText(CarDetailsActivity.this, "Failed to fetch vehicle details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupImageSlider(String mainImage, String frontImage, String backImage, String leftImage, String rightImage, String interiorImage) {
        String[] imageIds = {mainImage, frontImage, backImage, leftImage, rightImage, interiorImage};

        for (String imageId : imageIds) {
            horizontalImage = new ImageView(this);
            horizontalImage.setLayoutParams(new LinearLayout.LayoutParams(300, 150));

            Glide.with(this)
                    .load(imageId)
                    .error(R.drawable.car1)
                    .into(horizontalImage);

            horizontalImage.setOnClickListener(v ->
                    Glide.with(this)
                            .load(imageId)
                            .error(R.drawable.car1)
                            .into(mainCarImage)
            );

            imageContainer.addView(horizontalImage);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

}