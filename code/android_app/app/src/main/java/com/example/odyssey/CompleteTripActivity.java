package com.example.odyssey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CompleteTripActivity extends AppCompatActivity {

    private ImageView carImage, ratingStar, pdateImage, ptimeImage, rdateImage, rtimeImage;
    private TextView carName, carRating, pickupDatePickerHint, pickupTimePickerHint,
            returnDatePickerHint, returnTimePickerHint, totalDistance, numberOfPassengers,
            numberOfStoppages, additionalInfo, ownerName, ownerEmail, renterName, renterEmail,
            acFeatureAmount, distanceFeatureAmount, totalAmount, advanceAmount;
    private Button completeTripButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_trip);

        carImage = findViewById(R.id.car_image);
        carName = findViewById(R.id.car_name);
        carRating = findViewById(R.id.car_rating);
        ratingStar = findViewById(R.id.rating_star);
        pickupDatePickerHint = findViewById(R.id.pickup_date_picker_hint);
        pickupTimePickerHint = findViewById(R.id.Pickup_date_timePicker_hint);
        returnDatePickerHint = findViewById(R.id.return_date_picker_hint);
        returnTimePickerHint = findViewById(R.id.return_date_time_hint);
        totalDistance = findViewById(R.id.distance_feature_amount_value_1);
        numberOfPassengers = findViewById(R.id.total_amount_value_1);
        numberOfStoppages = findViewById(R.id.seats_amount_value);
        additionalInfo = findViewById(R.id.additional_info);
        ownerName = findViewById(R.id.owner_name);
        ownerEmail = findViewById(R.id.owner_email);
        renterName = findViewById(R.id.renter_name);
        renterEmail = findViewById(R.id.renter_email);
        acFeatureAmount = findViewById(R.id.ac_feature_amount_value);
        distanceFeatureAmount = findViewById(R.id.distance_feature_amount_value);
        totalAmount = findViewById(R.id.total_amount_value);
        advanceAmount = findViewById(R.id.advance_amount_value);
        completeTripButton = findViewById(R.id.advance_payment_button);

        // Example data setting, in a real application data will come from an intent or database
        carImage.setImageResource(R.drawable.car_pic);
        carName.setText("Toyota Axios");
        carRating.setText("4.9");
        ratingStar.setImageResource(R.drawable.ic_star);
        pickupDatePickerHint.setText("18 September, 2024");
        pickupDatePickerHint.setTextColor(Color.BLACK);
        pickupTimePickerHint.setText("12:12 AM");
        pickupTimePickerHint.setTextColor(Color.BLACK);
        returnDatePickerHint.setText("18 September, 2024");
        returnDatePickerHint.setTextColor(Color.BLACK);
        returnTimePickerHint.setText("12:12 AM");
        returnTimePickerHint.setTextColor(Color.BLACK);
        totalDistance.setText("30 Km");
        numberOfPassengers.setText("5");
        numberOfStoppages.setText("2");
        additionalInfo.setText("AC");
        ownerName.setText("Karim Hossain");
        ownerEmail.setText("karim@gmail.com");
        renterName.setText("Karim Hossain");
        renterEmail.setText("karim@gmail.com");
        acFeatureAmount.setText("20*5=100 ৳");
        distanceFeatureAmount.setText("100*30=3000 ৳");
        totalAmount.setText("3100 ৳");
        advanceAmount.setText("3100*50%=1600 ৳");


    }
}


