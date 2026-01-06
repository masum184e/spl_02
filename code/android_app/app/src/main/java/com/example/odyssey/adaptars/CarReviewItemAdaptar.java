package com.example.odyssey.adaptars;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssey.R;
import com.example.odyssey.models.CarReviewItemModel;

import java.util.List;

public class CarReviewItemAdaptar extends RecyclerView.Adapter<CarReviewItemAdaptar.CarReviewItemViewHolder>{
    private List<CarReviewItemModel> itemList;

    public CarReviewItemAdaptar(List<CarReviewItemModel> itemList) {
        this.itemList = itemList;
    }
    public class CarReviewItemViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, daysAgo, review;
        public ImageView image;
        public CarReviewItemViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.userName);
            daysAgo = view.findViewById(R.id.daysAgo);
            review = view.findViewById(R.id.review);
            image = view.findViewById(R.id.userAvatar);
        }
    }

    @NonNull
    @Override
    public CarReviewItemAdaptar.CarReviewItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_review_item, parent, false);
        return new CarReviewItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarReviewItemAdaptar.CarReviewItemViewHolder holder, int position) {
        CarReviewItemModel currentItem = itemList.get(position);
        holder.userName.setText(currentItem.getUserName());
        holder.daysAgo.setText(currentItem.getDaysAgo());
        holder.review.setText(currentItem.getReview());
        holder.image.setImageResource(currentItem.getImage());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
