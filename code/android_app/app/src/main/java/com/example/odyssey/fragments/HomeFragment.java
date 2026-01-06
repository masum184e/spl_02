package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.odyssey.R;
import com.example.odyssey.adaptars.HomeCarItemAdaptar;
import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.VehicleListResponse;
import com.example.odyssey.models.VehicleModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView homeCarItemContainer;
    private HomeCarItemAdaptar carItemAdapter;
    private List<VehicleModel> itemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeCarItemContainer = view.findViewById(R.id.home_car_item_container);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        homeCarItemContainer.setLayoutManager(layoutManager);
        carItemAdapter = new HomeCarItemAdaptar(itemList);
        homeCarItemContainer.setAdapter(carItemAdapter);

        fetchVehicles();

        return view;
    }

    private void fetchVehicles() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<VehicleListResponse> call = apiService.getAllVehicles();

        call.enqueue(new Callback<VehicleListResponse>() {

            @Override
            public void onResponse(Call<VehicleListResponse> call, Response<VehicleListResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    VehicleListResponse vehicleListResponse = response.body();
                    if ("true".equals(vehicleListResponse.getStatus())) {
                        itemList.clear();
                        itemList.addAll(vehicleListResponse.getData());
                        carItemAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), vehicleListResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch vehicles.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VehicleListResponse> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
