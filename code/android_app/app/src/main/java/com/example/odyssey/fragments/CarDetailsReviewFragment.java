package com.example.odyssey.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssey.R;
import com.example.odyssey.adaptars.CarReviewItemAdaptar;
import com.example.odyssey.models.CarReviewItemModel;

import java.util.ArrayList;
import java.util.List;

public class CarDetailsReviewFragment extends Fragment {
    private List<CarReviewItemModel> itemList = new ArrayList<>();
    private RecyclerView reviewItemContainer;
    private CarReviewItemAdaptar carReviewItemAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_car_details_review, container, false);

        reviewItemContainer = view.findViewById(R.id.reviewItemContainer);

        layoutManager = new LinearLayoutManager(getContext());
        reviewItemContainer.setLayoutManager(layoutManager);
        itemList.add(new CarReviewItemModel("User 1","1 days ago","Good",R.drawable.avatar));
        itemList.add(new CarReviewItemModel("User 2","2 days ago","Excelent",R.drawable.avatar));
        itemList.add(new CarReviewItemModel("User 3","3 days ago","Very Good",R.drawable.avatar));
        itemList.add(new CarReviewItemModel("User 4","4 days ago","Bad",R.drawable.avatar));
        itemList.add(new CarReviewItemModel("User 5","5 days ago","Average",R.drawable.avatar));

        carReviewItemAdapter = new CarReviewItemAdaptar(itemList);
        reviewItemContainer.setAdapter(carReviewItemAdapter);

        return view;
    }
}
