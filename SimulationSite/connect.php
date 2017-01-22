<?php
$host = "localhost";
$db = "thirstycircuits";
$user = "root";
$pass = "";

if (false) {
    $host = "localhost";
    $db = "familygiftlist";
    $user = "funnyuser";
    $pass = "funnypass";
}

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_errno) {
    echo "Failed to connect to MySQL: " . $conn->connect_error;
}
?>