package com.example.projet;

import static com.example.projet.HelpUser.remove_photo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projet.HelpUser;
import com.example.projet.ImageDownloader;
import com.example.projet.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class library_adaptor extends RecyclerView.Adapter<library_adaptor.MyViewHolder> {
    private ArrayList<String> photosLibraries = new ArrayList<>();
    Context c;

    public library_adaptor(ArrayList<String> librarymodel, Context c) {
        this.photosLibraries = librarymodel;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.item_photo_library, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String imageUrl = photosLibraries.get(position);
        Glide.with(c)
                .load(imageUrl)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (holder.Downloadbutton.getVisibility() == View.VISIBLE) {
                    holder.Downloadbutton.setVisibility(View.INVISIBLE);
                    holder.Removebutton.setVisibility(View.INVISIBLE);
                } else {
                    holder.Downloadbutton.setVisibility(View.VISIBLE);
                    holder.Removebutton.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.Downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageDownloader(v.getContext()).execute(imageUrl);

            }
        });
        holder.Removebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase d = FirebaseDatabase.getInstance();
                DatabaseReference r = d.getReference("users");
                HelpUser.get_username_connected(r, new HelpUser.UsernameCallback() {
                    @Override
                    public void onUsernameReceived(String username) {
                        remove_photo(r, username, imageUrl.toString());
                        removeItem(position);
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return photosLibraries.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button Downloadbutton;
        Button Removebutton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview1);
            Downloadbutton = itemView.findViewById(R.id.DownalodButton);
            Removebutton = itemView.findViewById(R.id.Remove);
        }
    }

    public void setData(List<String> photosLibraries) {
        this.photosLibraries = new ArrayList<>(photosLibraries);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        photosLibraries.remove(position);
        notifyItemRemoved(position);
    }
}
