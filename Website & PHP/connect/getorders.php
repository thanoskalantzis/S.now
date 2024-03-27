<?php
    header("Content-Type: application/json; charset=UTF-8");
    
    $afm_business = $_GET['afm_business'];

    // get Database information and details
    $DB_SERVER = $_GET['DB_SERVER'];
    $DB_NAME = $_GET['DB_NAME'];
    $DB_USER = $_GET['DB_USER'];
    $DB_PASSWORD = $_GET['DB_PASSWORD'];

    // array for JSON response
    $response = array();
    $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

    // get all orders from orders_afm table
    $command = "SELECT * FROM orders$afm_business";
    $result = $connection->query($command) or die (mysqli_connect_error());

    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        // looping through all results
        // orders node
        $response["orders"] = array();
    
        while ($row = mysqli_fetch_array($result)) {
            // new order array
            $order = array();
            $order["product_id"] = $row["product_id"];
            $order["category_id"] = $row["category_id"];
            $order["product_name"] = $row["product_name"];
            $order["description"] = $row["description"];
            $order["price"] = $row["price"];
            $order["quantity"] = $row["quantity"];
            $order["number_table"] = $row["number_table"];
            $order["date_time"] = $row["date_time"];
            $order["isActive"] = $row["isActive"];

            // push single order into final response array
            array_push($response["orders"], $order);
        }
        // success
        $response["success"] = 1;
    
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no orders found
        $response["success"] = 0;
        $response["message"] = "No orders found";
    
        // echo no orders JSON
        echo json_encode($response);
    }
?>