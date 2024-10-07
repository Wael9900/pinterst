// ImageAdapter.java
package com.example.projet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<Uri> imageUrls;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDownloadClick(int position); // Add method for handling download button clicks
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public ImageAdapter(Context context, List<Uri> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUrls.get(position);
        // Load image into ImageView using AsyncTask
        new LoadImageTask(holder.imageView).execute(imageUri.toString());

        // Set the background color based on selection
        if (selectedPosition == position) {
            //holder.imageView.setBackgroundResource(R.drawable.image_border);
        } else {
            holder.imageView.setBackground(null); // Remove background if not selected
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button btnDelete;
        Button btnDownload; // Add a reference to the download button

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDownload = itemView.findViewById(R.id.btnDownload); // Initialize the download button

            // Set click listener for the delete button
            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                    itemClickListener.onItemClick(position); // Notify activity about delete action
                }
            });

            // Set click listener for the download button
            btnDownload.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                    itemClickListener.onDownloadClick(position); // Notify activity about download action
                }
            });
        }
    }

    void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String imageUrl = strings[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream;
                if (imageUrl.startsWith("http")) {
                    // Load image from remote URL
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    inputStream = connection.getInputStream();
                } else {
                    // Load image from local file URI using content resolver
                    inputStream = imageView.getContext().getContentResolver().openInputStream(Uri.parse(imageUrl));
                }
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Log.e("LoadImageTask", "Failed to load image bitmap");
            }
        }
    }
}
