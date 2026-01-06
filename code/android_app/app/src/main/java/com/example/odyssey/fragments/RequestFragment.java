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
import android.widget.TextView;
import android.widget.Toast;

import com.example.odyssey.R;
import com.example.odyssey.SigninActivity;
import com.example.odyssey.adaptars.BookingReqListAdaptar;
import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.BookingListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RequestFragment extends Fragment {
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private String bearerToken;
    private BookingReqListAdaptar bookingReqListAdaptar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedPreferences = requireActivity().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        bearerToken = sharedPreferences.getString("authToken", null);

        if (bearerToken == null) {
            Toast.makeText(requireContext(), "Unauthorized User", Toast.LENGTH_LONG).show();
            logout();
        } else {
            fetchBookingList();
        }
        return view;
    }

    private void fetchBookingList() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<BookingListResponse> call = apiService.getBookingList("Bearer " + bearerToken);

        call.enqueue(new Callback<BookingListResponse>() {
            @Override
            public void onResponse(Call<BookingListResponse> call, Response<BookingListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!isAdded()) return;
                    BookingListResponse bookingListResponse = response.body();
                    bookingReqListAdaptar = new BookingReqListAdaptar(bookingListResponse.getData());
                    recyclerView.setAdapter(bookingReqListAdaptar);
                } else {
                    Toast.makeText(getContext(), "Failed to load Data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BookingListResponse> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "ERROR: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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