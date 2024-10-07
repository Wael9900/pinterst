package com.example.projet;


import static com.example.projet.HelpUser.get_username_connected;
import static com.example.projet.HelpUser.log_in;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.ProgressDialog;


public class login extends Fragment {
    FirebaseDatabase database;
    DatabaseReference reference;

    EditText editTextUsername, editTextPassword;
    Button buttonLogin, buttonSignUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");
        ProgressDialog p=new ProgressDialog(getContext());
        p.setMessage("Checking connection...");
        p.show();
        new Handler().postDelayed(() -> {
            get_username_connected(reference,new HelpUser.UsernameCallback() {
                @Override
                public void onUsernameReceived(String username) {
                    // Handle the received username here
                    if(username!=""){
                        loggedinFragment();

                    }
                    p.dismiss();
        }
            });
        },1000);


        View view = inflater.inflate(R.layout.fragment_login, container, false);
        editTextUsername = view.findViewById(R.id.editTextUsernameLogin);
        editTextPassword = view.findViewById(R.id.editTextPasswordLogin);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonSignUp = view.findViewById(R.id.buttonSignUp);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                reference.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                                HelpUser user = new HelpUser(dataSnapshot.child(username).child("name").getValue(String.class),dataSnapshot.child(username).child("email").getValue(String.class),dataSnapshot.child(username).child("password").getValue(String.class),dataSnapshot.child(username).child("connected").getValue(Boolean.class));
                                if (user != null && user.getPassword().equals(password)) {
                                    log_in(reference,user.getName());
                                    ProgressDialog progressDialog = new ProgressDialog(getContext());
                                    progressDialog.setMessage("Logging in...");
                                    progressDialog.show();
                                    new Handler().postDelayed(() -> {
                                        loggedinFragment();
                                        progressDialog.dismiss();
                                    }, 1000);

                                    return;
                                }

                            Toast.makeText(getActivity(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "User does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpFragment();
            }
        });

        return view;
    }

    private void signUpFragment() {
        SignUpFragment signUpFragment = new SignUpFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, signUpFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loggedinFragment() {
        logged_in loggedInFragment = new logged_in();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, loggedInFragment)
                .addToBackStack(null)
                .commit();
    }
}
