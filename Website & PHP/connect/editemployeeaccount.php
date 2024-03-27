<?php 
    header("Content-Type: application/json; charset=UTF-8");

    /*
    * Following code will edit an Employee Account accordingly
    * All necessary information and details on the Employee Account to be edited are retrieved from the original HTTP POST Request being made
    */
    
    // array for JSON response
    $response = array();

    // check if required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['case']) && isset($_POST['afm_employee']) ) {

        $case = $_POST['case'];
        $afm_employee = $_POST['afm_employee'];

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        if($case == 1){
            $new_password = $_POST['new_password'];
            
            $command = "UPDATE all_employees SET password = '$new_password' WHERE all_employees.afm_employee = $afm_employee";
            
            // executing command
            $result = $connection->query($command) or die (mysqli_connect_error()); 
            
            // check if command executed or not
            if($result){
                
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
        }else{
            $command_1 = "DELETE from working_employees where working_employees.afm_employee = $afm_employee";
            
            // executing command
            $result_1 = $connection->query($command_1) or die (mysqli_connect_error()); 
            
            $command_2 = "DELETE FROM all_employees WHERE all_employees.afm_employee = $afm_employee";
            
            // executing command
            $result_2 = $connection->query($command_2) or die (mysqli_connect_error()); 
            
            // check if commands executed or not
            if($result_1 && $result_2){
                
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
        }
        
    } else {
        // required field(s) is(are) missing
        $response["success"] = 0;
        $response["message"] = "Required field(s) is(are) missing";
    
        // echoing JSON response
        echo json_encode($response);
    }
?>	