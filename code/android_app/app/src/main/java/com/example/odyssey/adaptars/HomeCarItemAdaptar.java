package com.example.odyssey.adaptars;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.odyssey.CarDetailsActivity;
import com.example.odyssey.R;
import com.example.odyssey.models.VehicleModel;

import java.util.List;

public class HomeCarItemAdaptar extends RecyclerView.Adapter<HomeCarItemAdaptar.HomeCarItemViewHolder> {

    private List<VehicleModel> vehicleList;

    public HomeCarItemAdaptar(List<VehicleModel> vehicleList) {
        if (vehicleList != null) {
            this.vehicleList = vehicleList;
        }
    }

    public class HomeCarItemViewHolder extends RecyclerView.ViewHolder {
        private TextView homeCarItemTitle;
        private TextView carRating;
        private ImageView homeCarItemImage;

        public HomeCarItemViewHolder(@NonNull View itemView) {
            super(itemView);
            homeCarItemTitle = itemView.findViewById(R.id.home_car_item_title);
            carRating = itemView.findViewById(R.id.car_rating);
            homeCarItemImage = itemView.findViewById(R.id.home_car_item_image);
        }
    }

    @NonNull
    @Override
    public HomeCarItemAdaptar.HomeCarItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_car_card_item, parent, false);
        return new HomeCarItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCarItemAdaptar.HomeCarItemViewHolder holder, int position) {
        VehicleModel vehicle = vehicleList.get(position);

        if (vehicle != null) {
            holder.homeCarItemTitle.setText(vehicle.getModel());
            holder.carRating.setText(String.valueOf(vehicle.getDriverId()));

            Glide.with(holder.homeCarItemImage.getContext())
                    .load(vehicle.getMainImage())
                    .error(R.drawable.car1)
                    .into(holder.homeCarItemImage);

            holder.itemView.setOnClickListener(v -> {
                if (String.valueOf(vehicle.getVehicleId()) != null) {
                    Intent intent = new Intent(v.getContext(), CarDetailsActivity.class);
                    intent.putExtra("CAR_ID", String.valueOf(vehicle.getVehicleId()));
                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Vehicle ID is invalid!", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(holder.itemView.getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return vehicleList != null ? vehicleList.size() : 0;
    }
}