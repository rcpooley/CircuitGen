<?php
session_start();
error_reporting(E_ALL);
ini_set('display_errors', 1);
$target_file = $_FILES["image"]["tmp_name"];
//move_uploaded_file($_FILES["image"]["tmp_name"], 'uploads/' . basename($_FILES["image"]["name"]));

// initialise the curl request
$request = curl_init('http://circuitgen.tk:5001/upload');

// send a file
curl_setopt($request, CURLOPT_POST, true);
curl_setopt(
    $request,
    CURLOPT_POSTFIELDS,
    array(
        'file' => new CURLFile($target_file)
    ));

// output the response
curl_setopt($request, CURLOPT_RETURNTRANSFER, true);
$_SESSION['json'] = curl_exec($request);

// close the session
curl_close($request);

header('Location: display.php');
echo '<html><body><script>window.location.href = "display.php";</script></body></html>';
?>