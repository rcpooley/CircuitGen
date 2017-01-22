package com.circuits.android.circuits;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.v4.app.FragmentActivity;
import android.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.circuits.android.circuits.R.id.hw;


public class OpeningPage extends Activity {

    /**
     * Comments about permissions in Camera:
     * "If your application uses, but does not require a camera in order to function,
     * instead set android:required to false. In doing so, Google Play will allow devices
     * without a camera to download your application. It's then your responsibility to check
     * for the availability of the camera at runtime by calling hasSystemFeature(PackageManager.FEATURE_CAMERA).
     * If a camera is not available, you should then disable your camera features."
     * <p>
     * We used the "android.hardware.camera" user feature, and set the "required" part to "true".
     */

    NetworkTask onetask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_page);
    }

    final Handler handler = new Handler();

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
        // Check which request we're responding to
        Runnable decodeRunnable = new Runnable() {
            int counter = 0;

            @Override
            public void run() {
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

                    Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    TextView hw = (TextView) findViewById(R.id.hw);

                    if (myBitmap == null && counter < 20) {
                        handler.postDelayed(this, 200);
                    } else {
                        hw.setText(myBitmap.getHeight() + " " + myBitmap.getWidth());
                    }

                    ImageView myImage = (ImageView) findViewById(R.id.imageTaken);
                    myImage.setImageBitmap(myBitmap);
                }
            }
        };
        handler.postDelayed(decodeRunnable, 100);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static int counter = 0;
    String mCurrentPhotoPath;

    public void cameraIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        TextView errorMessage = (TextView) findViewById(R.id.errorTextView);

        File photoFile = null;

        try {
            photoFile = createImageFile();
        } catch (IOException ex) {

            errorMessage.setText("Error: File not made correctly!");
        }

        Uri photoURI = null;

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoURI = FileProvider.getUriForFile(this,
                    "com.circuits.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

        if (photoURI != null) {
            TextView working = (TextView) findViewById(R.id.workingornot);

            File imgFile = new File(mCurrentPhotoPath);

            working.setText(mCurrentPhotoPath);

            TextView exists = (TextView) findViewById(R.id.exists);

            exists.setText(imgFile.exists() + " ");

            if (imgFile.exists()) { //so file and picture DO exist... why can't I get them?

                System.out.println("Before Execute");
                if (onetask != null) {
                    onetask.destroy();
                }
                onetask = new NetworkTask();
                onetask.execute(mCurrentPhotoPath);
                System.out.println("After Execute");

                Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                TextView hw = (TextView) findViewById(R.id.hw);

                if (myBitmap != null) {
                    hw.setText(myBitmap.getHeight() + " " + myBitmap.getWidth());
                } else {
                    hw.setText("BitMap is null.");
                }


                ImageView myImage = (ImageView) findViewById(R.id.imageTaken);
                myImage.setImageBitmap(myBitmap);

            }
        }

    }

    private File createImageFile() throws IOException {

        String imageFileName = "JPEG_" + counter + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        counter++;
        return image;
    }

    public void goToOutput(View view) {
        Intent transitionIntent = new Intent(this, Output.class);
        startActivity(transitionIntent);
    }


    class NetworkTask extends AsyncTask<String, Void, String> {

        volatile boolean running = true;

        protected String doInBackground(String... urls) {
            try {
                System.out.println("doInBackground before");
                File image = new File(mCurrentPhotoPath);
                System.out.println("Waiting on the world to change");
                while (image.length() == 0 && running) {
                    Thread.sleep(500 / 2);
                }

                if (running) {
                    System.out.println("Its been taken!");
                    InputStream stream = new FileInputStream(image);
                    uploadImage(stream);
                    stream.close();

                } else {
                    System.out.println("Destroyed");
                }

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }

            return null;
        }

        public void uploadImage(InputStream stream) throws IOException {
            String url = "http://54.213.237.53:5000/";
            String charset = "UTF-8";
            String boundary = Long.toHexString(System.currentTimeMillis());
            String CRLF = "\r\n";

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (
                    OutputStream output = connection.getOutputStream();
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
            ) {
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"image.png\"").append(CRLF);
                writer.append("Content-Type: multipart/form-data").append(CRLF);
                writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                writer.append(CRLF).flush();

                int read = 0;
                byte[] buffer = new byte[1024];
                while ((read = stream.read(buffer)) > 0) {
                    output.write(buffer, 0, read);
                }

                output.flush();
                writer.append(CRLF).flush();
                writer.append("--" + boundary + "--").append(CRLF).flush();
            }

            int code = connection.getResponseCode();
            System.out.println(code);

            InputStream input;
            if (code == 200)
                input = connection.getInputStream();
            else
                input = connection.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        }

        public void destroy() {
            running = false;
        }
    }
}
