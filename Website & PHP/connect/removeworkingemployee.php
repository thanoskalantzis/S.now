<?php 
    header("Content-Type: application/json; charset=UTF-8");

    /*
    * Following code will delete an existing Working Employee out of the working_employees Table
    * All necessary information and details are retrieved from the original HTTP POST Request being made
    */
    
    // array for JSON response
    $response = array();

    // check if required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['afm_business']) && isset($_POST['afm_employee']) ) {

        $afm_business = $_POST['afm_business'];
        $afm_employee = $_POST['afm_employee'];

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        $command = "DELETE FROM working_employees WHERE working_employees.afm_employee = $afm_employee AND working_employees.afm_business = $afm_business";

        // executing the corresponding command
        $result = $connection->query($command) or die (mysqli_connect_error()); 

        // check if command executed successfully or not
        if ($result) {
            // successfully executed command
            $response["success"] = 1;
            $response["message"] = "Working employee successfully removed";
    
            // echoing JSON response
            echo json_encode($response);
        } else {
            // failed to execute command
            $response["success"] = 0;
            $response["message"] = "Failed to remove working employee";
    
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