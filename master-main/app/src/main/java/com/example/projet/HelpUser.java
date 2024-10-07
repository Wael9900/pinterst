package com.example.projet;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class HelpUser {
    String name;
    String email;
    String password;
    boolean connected;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isConnected() {
        return connected;
    }

    public HelpUser(String name, String email, String password, boolean connected) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.connected = connected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public interface UsernameCallback {
        void onUsernameReceived(String username);
    }

    public static void get_username_connected(DatabaseReference r, UsernameCallback callback) {
        r.orderByChild("connected").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.getChildren().iterator().next().child("name").getValue(String.class);
                    callback.onUsernameReceived(username);
                } else {
                    callback.onUsernameReceived("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    public static void log_out(DatabaseReference r) {
        r.orderByChild("connected").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot childSnapshot = dataSnapshot.getChildren().iterator().next();
                childSnapshot.getRef().child("connected").setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    public static void log_in(DatabaseReference r, String username) {
        r.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child(username).child("connected").setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    public static void insert_url(String username, DatabaseReference reference, String url) {
        reference.child(username).child("urls").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long count = dataSnapshot.getChildrenCount();
                    reference.child(username).child("urls").child(String.valueOf(count + 1)).setValue(url);

                } else {
                    reference.child(username).child("urls").child("1").setValue(url);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential error
            }
        });

    }

    public interface UrlsCallback {
        void onUrlsReceived(List<String> urls);
    }

    public static void getUrls(DatabaseReference reference,Context c, UrlsCallback callback) {
        get_username_connected(reference, new UsernameCallback() {
            @Override
            public void onUsernameReceived(String username) {
                if(username==""){
                    Toast.makeText(c,"you need to login to see your images",Toast.LENGTH_LONG).show();
                }
                DatabaseReference userUrlsRef = reference.child(username).child("urls");
                userUrlsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> urls = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                String url = String.valueOf(child.getValue(String.class));
                                urls.add(url);
                            }
                            callback.onUrlsReceived(urls);
                        }
                        else{
                            Toast.makeText(c,"you don't have any photo saved in your libraru ",Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle potential error
                        callback.onUrlsReceived(new ArrayList<>()); // Notify the callback with an empty list
                    }
                });
            }
        });
    }
    public static void saved(DatabaseReference r, String url, String username, OnExistsListener listener) {
        r.child(username).child("urls").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                if (snapshot.exists()){
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String value = child.getValue(String.class);
                        if (value != null && value.equals(url)) {
                            exists = true;
                            break;
                        }}

                }
                // Invoke the listener with the result
                listener.onExists(exists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                // For example, log the error
                Log.e("Firebase", "Database operation cancelled: " + error.getMessage());
            }
        });
    }

    // Define an interface to handle the result
    public interface OnExistsListener {
        void onExists(boolean exists);
    }
    public static void remove_photo(DatabaseReference r,String username,String Url){
        r.child(username).child("urls").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child: snapshot.getChildren()){
                    if (child.getValue(String.class).equals(Url)){
                        String childkey=child.getKey();
                        r.child(username).child("urls").child(childkey).removeValue();
                        break;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}




