package com.example.odyssey.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.odyssey.ChangePasswordActivity;
import com.example.odyssey.MakeComplainActivity;
import com.example.odyssey.R;
import com.example.odyssey.SigninActivity;
import com.example.odyssey.UploadVehicle;
import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.ProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private LinearLayout logoutBtn;
    private LinearLayout changePasswordBtn;
    private SharedPreferences sharedPreferences;
    private String bearerToken;
    private TextView profileName, mobileNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileName = view.findViewById(R.id.profile_name);
        mobileNumber = view.findViewById(R.id.mobile_number);
        logoutBtn = view.findViewById(R.id.logout_btn);
        changePasswordBtn = view.findViewById(R.id.changePasswordBtn);

        sharedPreferences = requireActivity().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        bearerToken = sharedPreferences.getString("authToken", null);

        changePasswordBtn.setOnClickListener(v -> startActivity(new Intent(v.getContext(), ChangePasswordActivity.class)));
        logoutBtn.setOnClickListener(v -> logout());

        if (bearerToken == null) {
            Toast.makeText(requireContext(), "Unauthorized User", Toast.LENGTH_LONG).show();
            logout();
        } else {
            fetchUserProfile();
        }

        return view;
    }

    private void fetchUserProfile() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ProfileResponse> call = apiService.getUserProfile("Bearer " + bearerToken);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profileResponse = response.body();
                    if ("true".equals(profileResponse.getStatus()) && profileResponse.getData() != null) {
                        profileName.setText(profileResponse.getData().getName());
                        mobileNumber.setText(profileResponse.getData().getMobileNumber());
                    } else {
                        Toast.makeText(requireContext(), profileResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch profile", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logout() {
        sharedPreferences.edit().remove("authToken").apply();
        Toast.makeText(requireContext(), "Logged Out Successfully", Toast.LENGTH_LONG).show();
        startActivity(new Intent(requireContext(), SigninActivity.class));
        requireActivity().finish();
    }
}