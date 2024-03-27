<?php 
    header("Content-Type: application/json; charset=UTF-8");

    /*
    * Following code will alter the number of tables of the specified Business
    * All necessary information and details are retrieved from the original HTTP POST Request being made
    */
    
    // array for JSON response
    $response = array();

    // check if all required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['afm_business']) && isset($_POST['number_tables']) ) {
    
        $afm_business = $_POST['afm_business'];
        $number_tables = $_POST['number_tables'];

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        $command = "UPDATE businesses SET number_tables = $number_tables where businesses.afm_business = $afm_business";

        // executing the above command
        $result = $connection->query($command) or die (mysqli_connect_error()); 
        
        // check if command executed successfully
        if ($result) {
            // successfully updated number of tables
            $response["success"] = 1;
            $response["message"] = "Number of tables successfully updated";
    
            // echoing JSON response
            echo json_encode($response);
        } else {
            // failed to update number of tables
            $response["success"] = 0;
            $response["message"] = "Failed to update number of tables";
    
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