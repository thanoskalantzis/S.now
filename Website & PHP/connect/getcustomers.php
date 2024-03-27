<?php
    header("Content-Type: application/json; charset=UTF-8");

    // Following code will list all the Customers

    // get Database information and details
    $DB_SERVER = $_GET['DB_SERVER'];
    $DB_NAME = $_GET['DB_NAME'];
    $DB_USER = $_GET['DB_USER'];
    $DB_PASSWORD = $_GET['DB_PASSWORD'];

    // array for JSON response
    $response = array();
    $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

    // get all customers from customers table
    $command = "SELECT * FROM customers";
    $result = $connection->query($command) or die (mysqli_connect_error());
    
    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        // looping through all results
        // customers node
        $response["customers"] = array();
    
        while ($row = mysqli_fetch_array($result)) {
            // new customer array
            $customer = array();
            $customer["first_name"] = $row["first_name"];
            $customer["last_name"] = $row["last_name"];
            $customer["email"] = $row["email"];
            $customer["password"] = $row["password"];
            $customer["phone"] = $row["phone"];

            // push single customer into final response array
            array_push($response["customers"], $customer);
        }
        // success
        $response["success"] = 1;
    
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no customer found
        $response["success"] = 0;
        $response["message"] = "No customer found";
    
        // echo no customer JSON
        echo json_encode($response);
    }
?>