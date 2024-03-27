<?php
    header("Content-Type: application/json; charset=UTF-8");

    // Following code will list all the Businesses

    // get Database information and details
    $DB_SERVER = $_GET['DB_SERVER'];
    $DB_NAME = $_GET['DB_NAME'];
    $DB_USER = $_GET['DB_USER'];
    $DB_PASSWORD = $_GET['DB_PASSWORD'];

    // array for JSON response
    $response = array();
    $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

    // get all businesses from businesses table
    $command = "SELECT * FROM businesses";
    $result = $connection->query($command) or die (mysqli_connect_error());

    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        // looping through all results
        // businesses node
        $response["businesses"] = array();
    
        while ($row = mysqli_fetch_array($result)) {
            // new business array
            $business = array();
            $business["email"] = $row["email"];
            $business["password"] = $row["password"];
            $business["business_id_name"] = $row["business_id_name"];
            $business["afm_business"] = $row["afm_business"];
            $business["phone"] = $row["phone"];
            $business["address"] = $row["address"];
            $business["postal_code"] = $row["postal_code"];
            $business["number_tables"] = $row["number_tables"];

            // push single business into final response array
            array_push($response["businesses"], $business);
        }
        // success
        $response["success"] = 1;
    
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no business found
        $response["success"] = 0;
        $response["message"] = "No business found";
    
        // echo no business JSON
        echo json_encode($response);
    }
?>