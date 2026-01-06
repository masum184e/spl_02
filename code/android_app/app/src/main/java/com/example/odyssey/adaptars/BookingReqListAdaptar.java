package com.example.odyssey.adaptars;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.odyssey.PaymentDetailsActivity;
import com.example.odyssey.PaymentGatewayActivity;
import com.example.odyssey.R;
import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.ApiResponse;
import com.example.odyssey.models.BookingListResponse;
import com.example.odyssey.models.BookingModel;
import com.example.odyssey.models.PaymentRequest;
import com.example.odyssey.models.PaymentResponse;
import com.example.odyssey.models.ProfileResponse;
import com.example.odyssey.models.UpdateStatusRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingReqListAdaptar extends RecyclerView.Adapter<BookingReqListAdaptar.BookingListViewHolder> {
    private List<BookingModel> bookingList;

    public BookingReqListAdaptar(List<BookingModel> bookingList) {
        if (bookingList != null) {
            this.bookingList = bookingList;
        }
    }

    @NonNull
    @Override
    public BookingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_req_list_item, parent, false);
        return new BookingListViewHolder(view);
    }

    public class BookingListViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView pickupDate;
        private LinearLayout actionContainer;
        private Button acceptBtn;
        private Button declineBtn;
        private Button pendingBtn;
        private WebView webView;


        public BookingListViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            pickupDate = itemView.findViewById(R.id.pickup_date);
            actionContainer = itemView.findViewById(R.id.action_container);
            acceptBtn = itemView.findViewById(R.id.accept_btn);
            declineBtn = itemView.findViewById(R.id.decline_btn);
            pendingBtn = itemView.findViewById(R.id.pending_btn);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BookingReqListAdaptar.BookingListViewHolder holder, int position) {
        BookingModel bookingData = bookingList.get(position);
        SharedPreferences sharedPreferences;
        String bearerToken;
        String userRole;

        if (bookingData != null) {

            holder.userName.setText(bookingData.getName());
            holder.pickupDate.setText(convertToDisplayFormat(bookingData.getPickup_datetime()));

            sharedPreferences = holder.itemView.getContext().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
            bearerToken = sharedPreferences.getString("authToken", null);
            userRole = sharedPreferences.getString("userRole", null);
            if ("renter".equals(userRole)) {
                if ("pending".equals(bookingData.getBooking_status())) {
                    holder.pendingBtn.setVisibility(View.VISIBLE);
                } else if ("progress".equals(bookingData.getBooking_status())) {
                    holder.pendingBtn.setVisibility(View.VISIBLE);
                    holder.pendingBtn.setText("Pay");
                    holder.pendingBtn.setOnClickListener(v -> {
                        makePaymentRequest(holder, bearerToken, String.valueOf(bookingData.getBooking_id()));
                    });
                } else if ("cancelled".equals(bookingData.getBooking_status())) {
                    holder.declineBtn.setVisibility(View.VISIBLE);
                    holder.declineBtn.setText("Cancelled");
                } else if ("paid".equals(bookingData.getBooking_status())) {
                    holder.acceptBtn.setVisibility(View.VISIBLE);
                    holder.acceptBtn.setText(bookingData.getBooking_status());
                }
            } else if ("driver".equals(userRole)) {
                if ("pending".equals(bookingData.getBooking_status())) {
                    holder.acceptBtn.setVisibility(View.VISIBLE);
                    holder.declineBtn.setVisibility(View.VISIBLE);
                    holder.acceptBtn.setOnClickListener(v -> {
                        makeUpdate(holder, bearerToken, new UpdateStatusRequest(String.valueOf(bookingData.getBooking_id()), "progress"));
                    });
                    holder.declineBtn.setOnClickListener(v -> {
                        makeUpdate(holder, bearerToken, new UpdateStatusRequest(String.valueOf(bookingData.getBooking_id()), "cancelled"));
                    });
                } else if ("progress".equals(bookingData.getBooking_status())) {
                    holder.pendingBtn.setVisibility(View.VISIBLE);
                    holder.pendingBtn.setText(bookingData.getBooking_status());
                } else if ("cancelled".equals(bookingData.getBooking_status())) {
                    holder.declineBtn.setVisibility(View.VISIBLE);
                    holder.declineBtn.setText("Cancelled");
                } else if ("paid".equals(bookingData.getBooking_status())) {
                    holder.acceptBtn.setVisibility(View.VISIBLE);
                    holder.acceptBtn.setText(bookingData.getBooking_status());
                }
            }
        } else {
            Toast.makeText(holder.itemView.getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }

    }

    private void makePaymentRequest(BookingListViewHolder holder, String bearerToken, String bookingId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<PaymentResponse> call = apiService.makePayment("Bearer " + bearerToken, new PaymentRequest(bookingId));
        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentResponse paymentResponse = response.body();
                    String status = paymentResponse.getStatus();
                    String message = paymentResponse.getMessage();
                    String url = paymentResponse.getUrl();

                    if ("true".equalsIgnoreCase(status) && url != null) {
                        openPaymentPage(holder, url);
                    } else {
                        Toast.makeText(holder.itemView.getContext(), message != null ? message : "Payment failed.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Unexpected response from server.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Log.e("PaymentError", "Error while making payment request", t);
                Toast.makeText(holder.itemView.getContext(), "Payment request failed! " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPaymentPage(BookingListViewHolder holder, String url) {
        Context context = holder.itemView.getContext();
        Intent intent = new Intent(context, PaymentGatewayActivity.class);
        intent.putExtra("paymentUrl", url);
        context.startActivity(intent);
    }


    private void makeUpdate(BookingListViewHolder holder, String bearerToken, UpdateStatusRequest updateStatusRequest) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiResponse> call = apiService.updateStatus("Bearer " + bearerToken, updateStatusRequest);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Failed to update status", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return bookingList != null ? bookingList.size() : 0;
    }

    public static String convertToDisplayFormat(String datetime) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
            LocalDateTime dateTime = LocalDateTime.parse(datetime, inputFormatter);
            return dateTime.format(displayFormatter);
        } catch (Exception e) {
            Log.e("DateConversionError", "Invalid date format: " + datetime, e);
            return "Invalid Date";
        }
    }

}
