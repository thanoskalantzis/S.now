<?php 
    header("Content-Type: application/json; charset=UTF-8");

    /*
    * Following code will add a new Message to messages Table
    * All Message and necessary information and details are retrieved from the original HTTP POST Request being made
    */
    
    // check if all required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['first_name']) && isset($_POST['last_name']) && isset($_POST['email']) && isset($_POST['phone']) && isset($_POST['title']) && isset($_POST['message']) ){

        $first_name = $_POST['first_name'];
        $last_name = $_POST['last_name'];
        $email = $_POST['email'];
        $phone = $_POST['phone'];
        $title = $_POST['title'];
        $message = $_POST['message'];

        $command = "insert into messages values ('$first_name', '$last_name', '$email', '$phone', '$title', '$message')";

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        // executing command
        $result = $connection->query($command) or die (mysqli_connect_error()); 
        
        // array for JSON response
        $response = array();
        
        if ($result) {
            // success
            $response["success"] = 1;
            $response["message"] = "New customer successfully added";
        
            // echoing JSON response
            echo json_encode($response);
        } else {
            // failed
            $response["success"] = 0;
            $response["message"] = "Failed to add new customer";
            
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