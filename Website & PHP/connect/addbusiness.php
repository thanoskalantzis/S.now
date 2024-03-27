<?php 
    header("Content-Type: application/json; charset=UTF-8");

    /*
    * Following code will create a new Business row
    * All necessary information and details are retrieved from the original HTTP POST Request being made
    */
    
    // array for JSON response
    $response = array();

    // check if all required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['email']) && isset($_POST['password']) && isset($_POST['business_id_name']) && isset($_POST['afm_business']) && isset($_POST['phone']) && isset($_POST['address']) && isset($_POST['postal_code']) && isset($_POST['number_tables']) ) {
    
        $email = $_POST['email'];
        $password = $_POST['password'];
        $business_id_name = $_POST['business_id_name'];
        $afm_business = $_POST['afm_business'];
        $phone = $_POST['phone'];
        $address = $_POST['address'];
        $postal_code = $_POST['postal_code'];
        $number_tables = $_POST['number_tables'];

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        $command = "INSERT INTO businesses VALUES('$email','$password','$business_id_name','$afm_business','$phone', '$address', '$postal_code', '$number_tables')";

        // inserting the new row
        $result = $connection->query($command) or die (mysqli_connect_error()); 
        
        // check if row inserted or not
        if ($result) {
            // successfully inserted new row into corresponding table
            $response["success"] = 1;
            $response["message"] = "Business successfully added";
    
            // echoing JSON response
            echo json_encode($response);
        } else {
            // failed to insert new row
            $response["success"] = 0;
            $response["message"] = "Failed to add new business";
    
            // echoing JSON response
            echo json_encode($response);
        }
    } else {
        // required field(s) is(are) missing
        $response["success"] = 0;
        $response["message"] = "Required field(s) is(are) missing";
    
        // echoing JSON response
        echo json_encode($response);
    }
?>	