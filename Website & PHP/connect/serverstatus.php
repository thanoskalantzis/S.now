<?php
    header("Content-Type: application/json; charset=UTF-8");

    /*  
    * The following code will return the server_status
    * In other words, if our Application can access the value of isOnline attribute out of the server_status table, then this means that the Host Server is indeed online and up and running
    */

    // get Database information and details
    $DB_SERVER = $_GET['DB_SERVER'];
    $DB_NAME = $_GET['DB_NAME'];
    $DB_USER = $_GET['DB_USER'];
    $DB_PASSWORD = $_GET['DB_PASSWORD'];

    // array for JSON response
    $response = array();
    $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

    // get isOnline value
    $command = "SELECT * FROM server_status";
    $result = $connection->query($command) or die (mysqli_connect_error());
    
    // check for empty result
    if (mysqli_num_rows($result) == 1) {
        $row = mysqli_fetch_array($result);
        
        // new status array
        $status = array();
        $status["isOnline"] = $row["isOnline"];
        
        // success
        $status["success"] = 1;
    
        // echoing JSON response
        echo json_encode($status);
    } else {
        $status["success"] = 0;
    
        // echoing JSON response
        echo json_encode($status);
    }
?>