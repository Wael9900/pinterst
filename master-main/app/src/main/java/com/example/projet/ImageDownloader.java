package com.example.projet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Void, Void> {
    private Context mContext;

    public ImageDownloader(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String imageUrl = params[0];
        try {
            // Create URL object from the image URL
            URL url = new URL(imageUrl);

            // Open connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            // Get input stream from the connection
            InputStream inputStream = connection.getInputStream();

            // Decode the input stream into a bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Close input stream
            inputStream.close();

            // Create a file to save the bitmap
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File imageFile = new File(directory, "downloaded_image.jpg");

            // Create output stream to write bitmap data to the file
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            // Compress the bitmap and write it to the output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // Close output stream
            outputStream.close();

            // Display a toast indicating successful download
            showToast("Image downloaded successfully");

        } catch (Exception e) {
            // Handle any errors
            e.printStackTrace();
            showToast("Failed to download image");
        }
        return null;
    }

    // Helper method to show a toast message
    private void showToast(final String message) {
        if (mContext != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
