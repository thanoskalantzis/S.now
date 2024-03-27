<?php
    header("Content-Type: application/json; charset=UTF-8");

    /* 
    * Following code will execute all 3 of the following commands.
    * afm value is essential to execute each command and this value will be provided through the HTTP POST Request.
    * All other necessary information and details are also retrieved from the original HTTP POST Request being made.
    */
    
    // array for JSON response
    $response = array();
    
    // check if required fields are provided
    if ( isset($_POST['DB_SERVER']) && isset($_POST['DB_NAME']) && isset($_POST['DB_USER']) && isset($_POST['DB_PASSWORD']) && isset($_POST['afm_business']) ) {
        $afm_business = $_POST['afm_business'];

        // get Database information and details
        $DB_SERVER = $_POST['DB_SERVER'];
        $DB_NAME = $_POST['DB_NAME'];
        $DB_USER = $_POST['DB_USER'];
        $DB_PASSWORD = $_POST['DB_PASSWORD'];

        $connection = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

        $command_1 = "create table products$afm_business(product_id int UNSIGNED unique not null AUTO_INCREMENT, category_id int unsigned not null, product_name varchar(50) not null, description varchar(200), price DECIMAL(5,2) unsigned not null, primary key (product_id), CONSTRAINT FOREIGN KEY (category_id) REFERENCES categories(category_id))";
        $command_2 = "ALTER TABLE products$afm_business AUTO_INCREMENT = 1";
        $command_3 = "create table orders$afm_business(product_id int unsigned not null, category_id int unsigned not null, product_name varchar(50) not null, description varchar(200), price DECIMAL(5,2) unsigned not null, quantity int unsigned not null, number_table int(3) unsigned not null, date_time TIMESTAMP NOT NULL, isActive boolean not null, CONSTRAINT FOREIGN KEY (product_id) REFERENCES products$afm_business(product_id))";

        // executing the given commands
        $result_1 = $connection->query($command_1) or die (mysqli_connect_error()); 
        $result_2 = $connection->query($command_2) or die (mysqli_connect_error()); 
        $result_3 = $connection->query($command_3) or die (mysqli_connect_error()); 

        // check if commands executed successfully
        if ($result_1 && $result_2 && $result_3) {
            // commands executed successfully 
            $response["success"] = 1;
            $response["message"] = "Commands executed successfully";
    
            // echoing JSON response
            echo json_encode($response);
        } else {
            // failed to execute commands
            $response["success"] = 0;
            $response["message"] = "Failed to execute commands";
    
            // echoing JSON response
            echo json_encode($response);
        }
    } else {
        //  problem(s) with required field(s)
        $response["success"] = 0;
        $response["message"] = "Problem(s) with required field(s)";
    
        // echoing JSON response
        echo json_encode($response);
    }
?>	