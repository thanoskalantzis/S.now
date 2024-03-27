<?php
    header("Content-Type: application/json; charset=UTF-8");

    // Following code will list all Employees that have registered to the Application

    // get Database information and details
    $DB_SERVER = $_GET['DB_SERVER'];
    $DB_NAME = $_GET['DB_NAME'];
    $DB_USER = $_GET['DB_USER'];
    $DB_PASSWORD = $_GET['DB_PASSWORD'];

    // array for JSON response
    $response = array();
    $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

    // get all employees from all_employees table
    $command = "SELECT * FROM all_employees";
    $result = $connection->query($command) or die (mysqli_connect_error());

    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        // looping through all results
        // all_employees node
        $response["all_employees"] = array();
    
        while ($row = mysqli_fetch_array($result)) {
            // new employee array
            $employee = array();
            $employee["afm_employee"] = $row["afm_employee"];
            $employee["first_name"] = $row["first_name"];
            $employee["last_name"] = $row["last_name"];
            $employee["email"] = $row["email"];
            $employee["password"] = $row["password"];
            $employee["phone"] = $row["phone"];

            // push single employee into final response array
            array_push($response["all_employees"], $employee);
        }
        // success
        $response["success"] = 1;
    
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no employee found
        $response["success"] = 0;
        $response["message"] = "No employee registered";
    
        // echo no employee JSON
        echo json_encode($response);
    }
?>