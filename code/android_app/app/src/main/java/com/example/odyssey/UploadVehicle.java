package com.example.odyssey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.odyssey.api.ApiService;
import com.example.odyssey.api.RetrofitClient;
import com.example.odyssey.models.UploadVehicleRequest;
import com.example.odyssey.models.UploadVehicleResponse;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadVehicle extends AppCompatActivity {

    private TextView tvOwnerProfile, tvMainImage, tvFrontImage, tvBackImage, tvLeftImage, tvRightImage, tvInteriorImage;
    private TextInputLayout licensePlateInputLayout, chassisNumberInputLayout, seatsInputLayout, modelNameInputLayout, mileageInputLayout, yearInputLayout, colorInputLayout;
    private Button btnOwnerProfile, btnMainImage, btnFrontImage, btnBackImage, btnLeftImage, btnRightImage, btnInteriorImage, submitButton;

    private SharedPreferences sharedPreferences;
    private String bearerToken;
    private String licensePlate, chassisNumber, seats, modelName, mileage, year, color;

    private Uri ownerProfileUri, mainImageUri, frontImageUri, backImageUri, leftImageUri, rightImageUri, interiorImageUri;
    private int currentRequestCode;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_vehcile);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Upload Vehicle");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize fields
        licensePlateInputLayout = findViewById(R.id.get_license_plate);
        chassisNumberInputLayout = findViewById(R.id.get_chassis_number);
        seatsInputLayout = findViewById(R.id.get_seats);
        modelNameInputLayout = findViewById(R.id.get_model_name);
        mileageInputLayout = findViewById(R.id.get_milage);
        yearInputLayout = findViewById(R.id.get_year);
        colorInputLayout = findViewById(R.id.get_color);

        btnOwnerProfile = findViewById(R.id.btn_owner_profile);
        btnMainImage = findViewById(R.id.btn_main_image);
        btnFrontImage = findViewById(R.id.btn_front_image);
        btnBackImage = findViewById(R.id.btn_back_image);
        btnLeftImage = findViewById(R.id.btn_left_image);
        btnRightImage = findViewById(R.id.btn_right_image);
        btnInteriorImage = findViewById(R.id.btn_interior_image);

        tvOwnerProfile = findViewById(R.id.tv_owner_profile);
        tvMainImage = findViewById(R.id.tv_main_image);
        tvFrontImage = findViewById(R.id.tv_front_image);
        tvBackImage = findViewById(R.id.tv_back_image);
        tvLeftImage = findViewById(R.id.tv_left_image);
        tvRightImage = findViewById(R.id.tv_right_image);
        tvInteriorImage = findViewById(R.id.tv_interior_image);

        sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        bearerToken = sharedPreferences.getString("authToken", null);

        // Button click listeners for file selection
        btnOwnerProfile.setOnClickListener(v -> selectImage(1));
        btnMainImage.setOnClickListener(v -> selectImage(2));
        btnFrontImage.setOnClickListener(v -> selectImage(3));
        btnBackImage.setOnClickListener(v -> selectImage(4));
        btnLeftImage.setOnClickListener(v -> selectImage(5));
        btnRightImage.setOnClickListener(v -> selectImage(6));
        btnInteriorImage.setOnClickListener(v -> selectImage(7));

        // Submit Button Listener
        submitButton = findViewById(R.id.upload_btn);
        submitButton.setOnClickListener(v -> validateAndSubmitForm());
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedFileUri = result.getData().getData();
                    updateSelectedFile(selectedFileUri);
                } else {
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            });

    private void selectImage(int requestCode) {
        currentRequestCode = requestCode;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra("requestCode", requestCode);
        imagePickerLauncher.launch(intent);
    }

    private void updateSelectedFile(Uri fileUri) {
        if (fileUri == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the file name from the URI
        String fileName = getFileNameFromUri(fileUri);

        switch (currentRequestCode) {
            case 1:
                ownerProfileUri = fileUri;
                tvOwnerProfile.setText(fileName); // Set the file name
                break;
            case 2:
                mainImageUri = fileUri;
                tvMainImage.setText(fileName);
                break;
            case 3:
                frontImageUri = fileUri;
                tvFrontImage.setText(fileName);
                break;
            case 4:
                backImageUri = fileUri;
                tvBackImage.setText(fileName);
                break;
            case 5:
                leftImageUri = fileUri;
                tvLeftImage.setText(fileName);
                break;
            case 6:
                rightImageUri = fileUri;
                tvRightImage.setText(fileName);
                break;
            case 7:
                interiorImageUri = fileUri;
                tvInteriorImage.setText(fileName);
                break;
        }
    }


    private void validateAndSubmitForm() {
        if (isFieldEmpty(licensePlateInputLayout, "License Plate") ||
                isFieldEmpty(chassisNumberInputLayout, "Chassis Number") ||
                isFieldEmpty(seatsInputLayout, "Seats") ||
                isFieldEmpty(modelNameInputLayout, "Model Name") ||
                isFieldEmpty(mileageInputLayout, "Mileage") ||
                isFieldEmpty(yearInputLayout, "Year") ||
                isFieldEmpty(colorInputLayout, "Color")) {
            return;
        }

        licensePlate = licensePlateInputLayout.getEditText().getText().toString();
        chassisNumber = chassisNumberInputLayout.getEditText().getText().toString();
        seats = seatsInputLayout.getEditText().getText().toString();
        modelName = modelNameInputLayout.getEditText().getText().toString();
        mileage = mileageInputLayout.getEditText().getText().toString();
        year = yearInputLayout.getEditText().getText().toString();
        color = colorInputLayout.getEditText().getText().toString();

        // Prepare files for upload
        MultipartBody.Part ownerImagePart = prepareFilePart("Owner Profile", ownerProfileUri);
        MultipartBody.Part mainImagePart = prepareFilePart("Main Image", mainImageUri);
        MultipartBody.Part frontImagePart = prepareFilePart("Front Image", frontImageUri);
        MultipartBody.Part backImagePart = prepareFilePart("Back Image", backImageUri);
        MultipartBody.Part leftImagePart = prepareFilePart("Left Image", leftImageUri);
        MultipartBody.Part rightImagePart = prepareFilePart("Right Image", rightImageUri);
        MultipartBody.Part interiorImagePart = prepareFilePart("Interior Image", interiorImageUri);

        String ownerNumber = "+8801400095352";
        // Prepare request object
        UploadVehicleRequest request = new UploadVehicleRequest(
                licensePlate, chassisNumber, seats, modelName, mileage, year, color, ownerNumber,
                ownerImagePart, mainImagePart, frontImagePart, backImagePart,
                leftImagePart, rightImagePart, interiorImagePart
        );

        System.out.println("License Plate -> " + licensePlate + "\n");
        System.out.println("Chasis Number -> " + chassisNumber + "\n");
        System.out.println("Number of Seats -> " + seats + "\n");
        System.out.println("Model Name -> " + modelName + "\n");
        System.out.println("Year -> " + year + "\n");
        System.out.println("Color -> " + color + "\n");
        System.out.println("Owner Image -> " + ownerImagePart + "\n");
        System.out.println("Main Image -> " + mainImagePart + "\n");
        System.out.println("Front Image -> " + frontImagePart + "\n");
        System.out.println("Back Image -> " + backImagePart + "\n");
        System.out.println("Left Image -> " + leftImagePart + "\n");
        System.out.println("Right Image -> " + rightImagePart + "\n");
        System.out.println("Interior Image -> " + interiorImagePart + "\n");

        Toast.makeText(this, "Form Submitted", Toast.LENGTH_LONG).show();

        createVehicle(request);
    }
    private RequestBody createRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private void createVehicle(UploadVehicleRequest uploadVehicleRequest) {
        ApiService apiService = RetrofitClient.getApiService();
                String ownerNumber = "+8801400095352";
        Call<UploadVehicleResponse> call = apiService.uploadVehicle(
                "Bearer " + bearerToken,
                createRequestBody(licensePlate),
                createRequestBody(chassisNumber),
                createRequestBody(seats),
                createRequestBody(modelName),
                createRequestBody(mileage),
                createRequestBody(year),
                createRequestBody(color),
                createRequestBody(ownerNumber),
                uploadVehicleRequest.getOwnerProfile(),
                uploadVehicleRequest.getMainImage(),
                uploadVehicleRequest.getFrontImage(),
                uploadVehicleRequest.getBackImage(),
                uploadVehicleRequest.getLeftImage(),
                uploadVehicleRequest.getRightImage(),
                uploadVehicleRequest.getInteriorImage()
        );

        call.enqueue(new Callback<UploadVehicleResponse>() {
            @Override
            public void onResponse(Call<UploadVehicleResponse> call, Response<UploadVehicleResponse> response) {
                Toast.makeText(UploadVehicle.this, "Form API Submitted", Toast.LENGTH_LONG).show();
                if (response.isSuccessful() && response.body() != null) {
                    UploadVehicleResponse uploadVehicleResponse = response.body();
                    if ("true".equals(uploadVehicleResponse.getStatus())) {
                        Toast.makeText(UploadVehicle.this, uploadVehicleResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(UploadVehicle.this, uploadVehicleResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UploadVehicle.this, "Failed to upload vehicle", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadVehicleResponse> call, Throwable t) {
                Toast.makeText(UploadVehicle.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        if (fileUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                String fileName = getFileNameFromUri(fileUri);

                RequestBody requestFile = RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        readBytes(inputStream)
                );

                return MultipartBody.Part.createFormData(partName, fileName, requestFile);

            } catch (FileNotFoundException e) {
                Toast.makeText(this, "File not found: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return null;
            } catch (IOException e) {
                Toast.makeText(this, "Error reading file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return null;
            }
        } else {
            Toast.makeText(this, partName + " not selected", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, bytesRead);
        }
        return byteBuffer.toByteArray();
    }


    private String getFileNameFromUri(Uri uri) {
        String fileName = "Unknown";
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        fileName = cursor.getString(nameIndex);
                    }
                } finally {
                    cursor.close();
                }
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }
        return fileName;
    }


    private boolean isFieldEmpty(TextInputLayout fieldLayout, String fieldName) {
        if (TextUtils.isEmpty(fieldLayout.getEditText().getText())) {
            fieldLayout.setError(fieldName + " is required");
            return true;
        } else {
            fieldLayout.setError(null);
        }
        return false;
    }
}
