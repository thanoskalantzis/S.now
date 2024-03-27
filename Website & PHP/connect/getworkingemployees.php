<?php
    header("Content-Type: application/json; charset=UTF-8");

    // Following code will list all currently Working Employees

    // get Database information and details
    $DB_SERVER = $_GET['DB_SERVER'];
    $DB_NAME = $_GET['DB_NAME'];
    $DB_USER = $_GET['DB_USER'];
    $DB_PASSWORD = $_GET['DB_PASSWORD'];

    // array for JSON response
    $response = array();
    $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

    // get all currently working employees from working_employees table
    $command = "SELECT * FROM working_employees";
    $result = $connection->query($command) or die (mysqli_connect_error());
    
    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        // looping through all results
        // working_employees node
        $response["working_employees"] = array();
    
        while ($row = mysqli_fetch_array($result)) {
            // new working_employee array
            $working_employee= array();
            $working_employee["afm_employee"] = $row["afm_employee"];
            $working_employee["afm_business"] = $row["afm_business"];

            // push single working_employee into final response array
            array_push($response["working_employees"], $working_employee);
        }
        // success
        $response["success"] = 1;
    
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no working employee found
        $response["success"] = 0;
        $response["message"] = "No currently working employee found";
    
        // echo no working employee JSON
        echo json_encode($response);
    }
?>