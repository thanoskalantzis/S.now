<?php 
    header("Content-Type: application/json; charset=UTF-8");

    /*
    * Following code will create a new Order row
    * All necessary information and details are retrieved from the original HTTP POST Request being made
    */
    
    // array for JSON response
    $response = array();

    // check if all required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['afm_business']) && isset($_POST['product_id']) && isset($_POST['category_id']) && isset($_POST['product_name']) && isset($_POST['description']) && isset($_POST['price']) && isset($_POST['quantity']) && isset($_POST['number_table']) && isset($_POST['date_time']) && isset($_POST['isActive']) ) {
    
        $afm_business = $_POST['afm_business'];
        $product_id = $_POST['product_id'];
        $category_id = $_POST['category_id'];
        $product_name = $_POST['product_name'];
        $description = $_POST['description'];
        $price = $_POST['price'];
        $quantity = $_POST['quantity'];
        $number_table = $_POST['number_table'];
        $date_time = $_POST['date_time'];
        $isActive = $_POST['isActive'];

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        $command = "INSERT INTO orders$afm_business VALUES ('$product_id', '$category_id', '$product_name', '$description', '$price','$quantity','$number_table','$date_time', '$isActive')";

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