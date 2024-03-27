<?php 
    header("Content-Type: application/json; charset=UTF-8");
    /*
    * Following code will update the price of an existing Product
    * All necessary information and details are retrieved from the original HTTP POST Request being made
    */
    
    // array for JSON response
    $response = array();

    // check if required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['afm_business']) && isset($_POST['product_id']) && isset($_POST['price_new']) ) {

        $afm_business = $_POST['afm_business'];
        $product_id = $_POST['product_id'];
        $price_new = $_POST['price_new'];

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        $command_1 = "UPDATE products$afm_business SET price = $price_new WHERE products$afm_business.product_id = $product_id";
        $command_2 = "UPDATE orders$afm_business SET price = $price_new WHERE orders$afm_business.product_id = $product_id";

        // executing the update command
        $result_1 = $connection->query($command_1) or die (mysqli_connect_error()); 
        $result_2 = $connection->query($command_2) or die (mysqli_connect_error());

        // check if update command executed successfully
        if ($result_1 && $result_2) {
            // successfully executed update command
            $response["success"] = 1;
            $response["message"] = "Product price successfully updated";
    
            // echoing JSON response
            echo json_encode($response);
        } else {
            // failed to update product price
            $response["success"] = 0;
            $response["message"] = "Failed to update product price";
    
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