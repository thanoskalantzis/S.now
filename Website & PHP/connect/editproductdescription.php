<?php 
    header("Content-Type: application/json; charset=UTF-8");

    /*
    * Following code will update the description of an existing Product
    * All necessary information and details are retrieved from the original HTTP POST Request being made
    */
    
    // array for JSON response
    $response = array();

    // check if required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['afm_business']) && isset($_POST['product_id']) && isset($_POST['description']) ) {

        $afm_business = $_POST['afm_business'];
        $product_id = $_POST['product_id'];
        $description = $_POST['description'];

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        $command_1 = "UPDATE products$afm_business SET description = '$description' WHERE products$afm_business.product_id = $product_id";
        $command_2 = "UPDATE orders$afm_business SET description = '$description' WHERE orders$afm_business.product_id = $product_id";

        // executing the update commands
        $result_1 = $connection->query($command_1) or die (mysqli_connect_error()); 
        $result_2 = $connection->query($command_2) or die (mysqli_connect_error()); 

        // check if update commands executed successfully
        if ($result_1 && $result_2) {
            // successfully executed update commands
            $response["success"] = 1;
            $response["message"] = "Product description successfully updated";
    
            // echoing JSON response
            echo json_encode($response);
        } else {
            // failed to update product description
            $response["success"] = 0;
            $response["message"] = "Failed to update product description";
    
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