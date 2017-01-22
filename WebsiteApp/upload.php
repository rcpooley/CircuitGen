<?php
$target_file = basename($_FILES["image"]["name"]);

// initialise the curl request
$request = curl_init('http://54.213.237.53:5001/upload');

// send a file
curl_setopt($request, CURLOPT_POST, true);
curl_setopt(
    $request,
    CURLOPT_POSTFIELDS,
    array(
        'file' => '@' . realpath($target_file)
    ));

// output the response
curl_setopt($request, CURLOPT_RETURNTRANSFER, true);
echo curl_exec($request);

echo '<br>Error: ' . curl_error($request);
// close the session
curl_close($request);

?>