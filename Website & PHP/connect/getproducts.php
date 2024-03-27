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

    // get all products from products_afm table
    $command = "SELECT * FROM products$afm_business";
    $result = $connection->query($command) or die (mysqli_connect_error());

    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        // looping through all results
        // products node
        $response["products"] = array();
    
        while ($row = mysqli_fetch_array($result)) {
            // new product array
            $product = array();
            $product["product_id"] = $row["product_id"];
            $product["category_id"] = $row["category_id"];
            $product["product_name"] = $row["product_name"];
            $product["description"] = $row["description"];
            $product["price"] = $row["price"];

            // push single product into final response array
            array_push($response["products"], $product);
        }
        // success
        $response["success"] = 1;
    
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no products found
        $response["success"] = 0;
        $response["message"] = "No business found";
    
        // echo no products JSON
        echo json_encode($response);
    }
?>