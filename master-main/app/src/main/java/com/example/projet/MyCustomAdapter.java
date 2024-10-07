package com.example.projet;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.projet.HelpUser.get_username_connected;
import static com.example.projet.HelpUser.insert_url;
import static com.example.projet.HelpUser.saved;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.projet.PictureDAO;
import com.example.projet.Photo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.ViewHolder> {

    private List<Photo> photos;
    private Context context;
    FirebaseDatabase database;
    DatabaseReference reference;

    public MyCustomAdapter(Context context, List<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        String imageUrl = photo.getUrls().getRegular();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .centerCrop()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of the save button
                if (holder.saveButton.getVisibility() == View.VISIBLE) {
                    holder.saveButton.setVisibility(View.INVISIBLE);
                } else {
                    holder.saveButton.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");
                ProgressDialog p=new ProgressDialog(holder.itemView.getContext());
                p.setMessage("savind data...");
                p.show();
                new Handler().postDelayed(() -> {
                    get_username_connected(reference,new HelpUser.UsernameCallback() {
                        @Override
                        public void onUsernameReceived(String username) {

                            if(username==""){
                                Toast.makeText(p.getContext(),"you need to login to save photos in your library" ,Toast.LENGTH_SHORT).show();
                            }
                            else{
                                saved(reference,imageUrl.toString(),username,new HelpUser.OnExistsListener() {
                                    @Override
                                    public void onExists(boolean exists) {
                                        if (exists) {
                                            Toast.makeText(p.getContext(), "photo already saved in your library",Toast.LENGTH_SHORT).show();


                                        } else {
                                            insert_url(username,reference,imageUrl.toString());
                                        }
                                    }
                                });

                            }
                            p.dismiss();
                        }
                    });
                },1000);


                // Handle save button click
                /*saveImageToLibrary(imageUrl);*/
            }
        });
    }


    private void saveImageToLibrary(String imageUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String uniqueFileName = generateUniqueFileName();
        StorageReference imageRef = storageRef.child("images/" + uniqueFileName + ".jpg");

        // Load the image using Glide into a SimpleTarget
        Glide.with(context)
                .as(byte[].class)
                .load(imageUrl)
                .into(new CustomTarget<byte[]>() {
                    @Override
                    public void onResourceReady(@NonNull byte[] resource, Transition<? super byte[]> transition) {
                        // Upload the byte array to Firebase Storage
                        UploadTask uploadTask = imageRef.putBytes(resource);
                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                            // Image uploaded successfully, now add URL to database
                            addImageUrlToDatabase(imageUrl);
                        }).addOnFailureListener(exception -> {
                            // Handle failed upload
                            Toast.makeText(context, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Not needed
                    }
                });
    }

    private void addImageUrlToDatabase(String imageUrl) {
        PictureDAO pictureDAO = new PictureDAO(context);
        pictureDAO.open();
        pictureDAO.insertURL(imageUrl);
        pictureDAO.close();
        Toast.makeText(context, "Image saved to library!", Toast.LENGTH_SHORT).show();
    }

    private String generateUniqueFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String randomString = UUID.randomUUID().toString();
        return "image_" + timeStamp + "_" + randomString;
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button saveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            saveButton = itemView.findViewById(R.id.saveButton);
        }
    }
}
