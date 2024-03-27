<?php 
    header("Content-Type: application/json; charset=UTF-8");
    /*
    * Following code will edit a Customer Account accordingly
    * All necessary information and details on the Customer Account to be edited are retrieved from the original HTTP POST Request being made
    */
    
    // array for JSON response
    $response = array();

    // check if required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['case']) && isset($_POST['email']) ) {
    
        $case = $_POST['case'];
        $email = $_POST['email'];

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        if($case == 1){
            $new_password = $_POST['new_password'];
            
            $command = "UPDATE customers SET password = '$new_password' WHERE customers.email = '$email'";
        }else{
            $command = "DELETE FROM customers WHERE customers.email = '$email'";
        }

        // executing command
        $result = $connection->query($command) or die (mysqli_connect_error()); 
        
        // check if command executed or not
        if ($result) {
            // successfully executed the command
            $response["success"] = 1;
            $response["message"] = "Command successfully executed";
    
            // echoing JSON response
            echo json_encode($response);
        } else {
            // failed to execute command
            $response["success"] = 0;
            $response["message"] = "Failed to execute the command";
    
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