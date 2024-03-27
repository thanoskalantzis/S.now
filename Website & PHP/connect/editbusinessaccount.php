<?php 
    header("Content-Type: application/json; charset=UTF-8");

    /*
    * Following code will edit a Business Account accordingly
    * All necessary information and details on the actual Business Account to be edited are retrieved from the original HTTP POST Request being made
    */
    
    // array for JSON response
    $response = array();

    // check if all required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['case']) && isset($_POST['afm_business']) ) {
    
        $case = $_POST['case'];
        $afm_business = $_POST['afm_business'];

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        if($case == 1){
            $new_password = $_POST['new_password'];
            
            $command = "UPDATE businesses SET password = '$new_password' WHERE businesses.afm_business = $afm_business";
            
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
        }elseif ($case == 2){
            $new_id_name = $_POST['new_id_name'];
            
            $command = "UPDATE businesses SET business_id_name = '$new_id_name' WHERE businesses.afm_business = $afm_business";
            
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
        }elseif ($case == 3){
            $new_address = $_POST['new_address'];
            
            $command = "UPDATE businesses SET address = '$new_address' WHERE businesses.afm_business = $afm_business";
            
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
        }elseif ($case == 4){
            $new_postal = $_POST['new_postal'];
            
            $command = "UPDATE businesses SET postal_code = $new_postal WHERE businesses.afm_business = $afm_business";
            
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
        }elseif ($case == 5){
            $new_phone= $_POST['new_phone'];
            
            $command = "UPDATE businesses SET phone = $new_phone WHERE businesses.afm_business = $afm_business";
            
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
        }elseif ($case == 6){
            $new_email= $_POST['new_email'];
            
            $command = "UPDATE businesses SET email = '$new_email' WHERE businesses.afm_business = $afm_business";
            
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
        }elseif ($case == 7){
            $command_1 = "DELETE from working_employees where working_employees.afm_business = $afm_business";
            
            // executing command
            $result_1 = $connection->query($command_1) or die (mysqli_connect_error()); 
            
            $command_2 = "TRUNCATE orders$afm_business";
            
            // executing command
            $result_2 = $connection->query($command_2) or die (mysqli_connect_error()); 
            
            $command_3 = "drop table orders$afm_business";
            
            // executing command
            $result_3 = $connection->query($command_3) or die (mysqli_connect_error());
            
            $command_4 = "TRUNCATE products$afm_business";
            
            // executing command
            $result_4 = $connection->query($command_4) or die (mysqli_connect_error());
            
            $command_5 = "drop table products$afm_business";
            
            // executing command
            $result_5 = $connection->query($command_5) or die (mysqli_connect_error());
            
            $command_6 = "delete from businesses where businesses.afm_business = $afm_business";
            
            // executing command
            $result_6 = $connection->query($command_6) or die (mysqli_connect_error());
            
            // check if commands executed or not
            if($result_1 && $result_2 && $result_3 && $result_4 && $result_5 && $result_6){
                
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
            
        }
        
    } else {
        // required field(s) is(are) missing
        $response["success"] = 0;
        $response["message"] = "Required field(s) is(are) missing";
    
        // echoing JSON response
        echo json_encode($response);
    }
?>	