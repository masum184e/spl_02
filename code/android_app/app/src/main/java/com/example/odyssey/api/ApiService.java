package com.example.odyssey.api;

import com.example.odyssey.models.AmountRequest;
import com.example.odyssey.models.AmountResponse;
import com.example.odyssey.models.BookingListResponse;
import com.example.odyssey.models.ApiResponse;
import com.example.odyssey.models.BookingRequest;
import com.example.odyssey.models.BookingResponse;
import com.example.odyssey.models.ComplaintsRequest;
import com.example.odyssey.models.ComplaintsResponse;
import com.example.odyssey.models.LoginRequest;
import com.example.odyssey.models.LoginResponse;
import com.example.odyssey.models.PasswordRequest;
import com.example.odyssey.models.PaymentRequest;
import com.example.odyssey.models.PaymentResponse;
import com.example.odyssey.models.ProfileResponse;
import com.example.odyssey.models.RegistrationRequest;
import com.example.odyssey.models.RegistrationResponse;
import com.example.odyssey.models.TakeLeaveRequest;
import com.example.odyssey.models.UnavailableDateListResponse;
import com.example.odyssey.models.UpdateStatusRequest;
import com.example.odyssey.models.UploadVehicleRequest;
import com.example.odyssey.models.UploadVehicleResponse;
import com.example.odyssey.models.VehicleListResponse;
import com.example.odyssey.models.VehicleResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @POST("authentication/login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("authentication/registration.php")
    Call<RegistrationResponse> userRegistration(@Body RegistrationRequest registrationRequest);

    @GET("authentication/profile.php")
    Call<ProfileResponse> getUserProfile(@Header("Authorization") String authToken);

    @POST("authentication/change-password.php")
    Call<ApiResponse> updatePassword(@Header("Authorization") String token, @Body PasswordRequest passwordRequest );

    @GET("vehicle/vehicle-list.php")
    Call<VehicleListResponse> getAllVehicles();

    @GET("vehicle/vehicle-list.php")
    Call<VehicleResponse> getVehicleById(@Query("vehicleId") int vehicleId);

    @POST("bookings/bookings-request.php")
    Call<ApiResponse> createBooking(@Header("Authorization") String authToken, @Body BookingRequest bookingRequest);

    @Multipart
    @POST("vehicle/upload-vehicle.php")
    Call<UploadVehicleResponse> uploadVehicle(
            @Header("Authorization") String token,
            @Part("license_plate_number") RequestBody licensePlate,
            @Part("chasis_number") RequestBody chassisNumber,
            @Part("number_of_seats") RequestBody seats,
            @Part("model") RequestBody modelName,
            @Part("mileage") RequestBody mileage,
            @Part("year") RequestBody year,
            @Part("color") RequestBody color,
            @Part("owner_mobile_number") RequestBody ownerNumber,
            @Part MultipartBody.Part owner_image,
            @Part MultipartBody.Part main_image,
            @Part MultipartBody.Part front_image,
            @Part MultipartBody.Part back_image,
            @Part MultipartBody.Part left_image,
            @Part MultipartBody.Part right_image,
            @Part MultipartBody.Part interior_image
    );

    @GET("bookings/booking-list.php")
    Call<BookingListResponse> getBookingList(@Header("Authorization") String authToken);

    @POST("complaints/make-complaints.php")
    Call<ComplaintsResponse> makeComplaints(@Header("Authorization") String authToken, @Body ComplaintsRequest complaintsRequest);

    @POST("payment/pay.php")
    Call<PaymentResponse> makePayment(@Header("Authorization") String authToken, @Body PaymentRequest paymentRequest);

    @POST("availability/take-leave.php")
    Call<ApiResponse> makeLeaveRequest(@Header("Authorization") String authToken, @Body TakeLeaveRequest takeLeaveRequest);

    @GET("availability/unavailable-dates.php")
    Call<UnavailableDateListResponse> getUnavailableDates(@Header("Authorization") String authToken);

    @POST("bookings/amount.php")
    Call<AmountResponse> checkAmount(@Header("Authorization") String authToken, @Body AmountRequest amountRequest);

    @POST("bookings/update-status.php")
    Call<ApiResponse> updateStatus(@Header("Authorization") String authToken, @Body UpdateStatusRequest updateStatusRequest);
}

