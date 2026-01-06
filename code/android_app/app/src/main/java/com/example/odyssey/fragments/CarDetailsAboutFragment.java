package com.example.odyssey.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.odyssey.R;

public class CarDetailsAboutFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_car_details_about, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String carDescription = args.getString("CAR_DESCRIPTION");
            TextView descriptionTextView = view.findViewById(R.id.car_description_text);
            descriptionTextView.setText(carDescription);
        }

        return view;
    }
}
