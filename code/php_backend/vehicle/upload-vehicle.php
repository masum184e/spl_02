<?php
    header('Content-Type: application/json');
    require './../database_connection.php';
    require './../config.php';
    require './../middleware/user_authentication.php';

    $user = userAuthentication();
    $email = $user->email;
    $role = $user->role;

    if ($_SERVER["REQUEST_METHOD"] !== "POST" || $role=="renter") {
        echo json_encode(["status" => "false", "message" => "Invalid Request."]);
        exit;
    }

    $query = "SELECT driver_id FROM drivers WHERE email = ?";
    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param($stmt, "s", $email);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);
    $row = mysqli_fetch_assoc($result);
    
    if (!$row) {
        echo json_encode(["status" => "false", "message" => "Driver not found."]);
        exit;
    }
    
    $driverId = $row['driver_id'];

    $licensePlate = $_POST['license_plate_number'] ?? '';
    $chasisNumber = $_POST['chasis_number'] ?? '';
    $seats = $_POST['number_of_seats'] ?? 0;
    $model = $_POST['model'] ?? '';
    $mileage = $_POST['mileage'] ?? 0;
    $year = $_POST['year'] ?? '';
    $type = $_POST['type'] ?? '';
    $color = $_POST['color'] ?? '';
    $ownerMobile = $_POST['owner_mobile_number'] ?? '';

    $vehicleData = [
        "driver_id" => $driverId,
        "license_plate_number" => $licensePlate,
        "chasis_number" => $chasisNumber,
        "number_of_seats" => $seats,
        "model" => $model,
        "mileage" => $mileage,
        "year" => $year,
        "type" => $type,
        "color" => $color,
        "owner_mobile_number" => $ownerMobile
    ];


    if (empty($licensePlate) || empty($mileage) || empty($seats) || empty($chasisNumber) || empty($model) || empty($year) || empty($color) || empty($ownerMobile)) {
        echo json_encode(["status" => "false", "message" => "All fields are required."]);
        exit;
    }
    $type="luxury";

    $uploadDir = './../uploads/vehicles/';
    if (!is_dir($uploadDir)) {
        mkdir($uploadDir, 0777, true);
    }

        // // Define file path
        // $fileName = "./{$driverId}.json";
        // file_put_contents($fileName, $vehicleJson);

    function uploadVehicleImage($image, $imageName, $uploadDir) {
        $fileTmpName = $image['tmp_name'];
        $fileName = basename($image['name']);
        $fileSize = $image['size'];
        $fileError = $image['error'];
        $fileExtension = strtolower(pathinfo($fileName, PATHINFO_EXTENSION));

        $allowedTypes = ['jpg', 'jpeg', 'png'];
        $fileType = mime_content_type($fileTmpName);

        if (!in_array($fileExtension, $allowedTypes) || !in_array($fileType, ['image/jpeg', 'image/png'])) {
            echo json_encode(["status" => "false", "message" => "$imageName: Only JPG, JPEG, PNG files are allowed."]);
            exit;
        }

        if ($fileSize > 2 * 1024 * 1024) {
            echo json_encode(["status" => "false", "message" => "$imageName: File size must be less than 2MB."]);
            exit;
        }

        if ($fileError !== 0) {
            echo json_encode(["status" => "false", "message" => "$imageName: File upload error: $fileError"]);
            exit;
        }

        $uniqueFileName = uniqid($imageName . '_', true) . '.' . $fileExtension;
        $targetFilePath = $uploadDir . $uniqueFileName;

        if (move_uploaded_file($fileTmpName, $targetFilePath)) {
            return $uniqueFileName;
        } else {
            echo json_encode(["status" => "false", "message" => "$imageName: Failed to move the uploaded file."]);
            exit;
        }
    }

    // $ownerImage = uploadVehicleImage($_FILES['owner_image'], 'owner_image', $uploadDir);
    // $mainImage = uploadVehicleImage($_FILES['main_image'], 'main_image', $uploadDir);
    // $frontImage = uploadVehicleImage($_FILES['front_image'], 'front_image', $uploadDir);
    // $backImage = uploadVehicleImage($_FILES['back_image'], 'back_image', $uploadDir);
    // $leftImage = uploadVehicleImage($_FILES['left_image'], 'left_image', $uploadDir);
    // $rightImage = uploadVehicleImage($_FILES['right_image'], 'right_image', $uploadDir);
    // $interiorImage = uploadVehicleImage($_FILES['interior_image'], 'interior_image', $uploadDir);

    function uploadVehicleImages($images, $uploadDir) {
        $uploadedFiles = [];
        foreach ($images as $key => $image) {
            $uploadedFiles[$key] = uploadVehicleImage($image, $key, $uploadDir);
        }
        return $uploadedFiles;
    }
    
    // Collect files to upload
    $vehicleImages = [
        'owner_image' => $_FILES['owner_image'],
        'main_image' => $_FILES['main_image'],
        'front_image' => $_FILES['front_image'],
        'back_image' => $_FILES['back_image'],
        'left_image' => $_FILES['left_image'],
        'right_image' => $_FILES['right_image'],
        'interior_image' => $_FILES['interior_image']
    ];
    
    // Upload images
    $imagePaths = uploadVehicleImages($vehicleImages, $uploadDir);

    // print_r($imagePaths);

        // // Define file path
        $fileName = "./{$driverId}_2.json";
        file_put_contents($fileName, $imagePaths);
    
    // $sql = "INSERT INTO vehicles (driver_id, license_plate_number, mileage, number_of_seats, chasis_number, model, type, year, color, owner_mobile_number, owner_image, main_image, front_image, back_image, left_image, interior_image, right_image) 
    //         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // $stmt = $conn->prepare($sql);
    // $stmt->bind_param("isdisdsssssssssss", $driverId, $licensePlate, $mileage, $seats, $chasisNumber, $model, $type, $year, $color, $ownerMobile, $ownerImage, $mainImage, $frontImage, $backImage, $leftImage, $interiorImage, $rightImage);

    // if ($stmt->execute()) {
        echo json_encode(["status" => "true", "message" => "Vehicle added successfully."]);
    //     exit;
    // } else {
    //     echo json_encode(["status" => "false", "message" => "Failed to insert vehicle details into the database."]);
    //     exit;
    // }

    $stmt->close();
    $conn->close();
?>
