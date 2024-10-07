package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
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

public class SignUpFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference reference;

    EditText editTextUsername, editTextEmail, editTextPassword;
    Button buttonSignUp, buttonReturnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        editTextUsername = view.findViewById(R.id.editTextUsernameSignUp);
        editTextEmail = view.findViewById(R.id.editTextEmailSignUp);
        editTextPassword = view.findViewById(R.id.editTextPasswordSignUp);
        buttonSignUp = view.findViewById(R.id.buttonSignUp);
        buttonReturnLogin = view.findViewById(R.id.buttonReturnLogin);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                // Check if username contains space
                if (username.contains(" ")) {
                    editTextUsername.setError("Username must not contain spaces");
                    return;
                }

                // Check password length
                if (password.length() < 8) {
                    editTextPassword.setError("Password must be at least 8 characters long");
                    return;
                }

                // Check email format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Invalid email format");
                    return;
                }

                // Check email domain
                if (!(email.endsWith("@gmail.com") || email.endsWith("@yahoo.fr") || email.endsWith("@outlook.fr"))) {
                    editTextEmail.setError("Supported email domains are: gmail.com, yahoo.fr, outlook.fr");
                    return;
                }

                reference.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            editTextUsername.setError("username already exists");
                        }
                        else{
                            reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        editTextEmail.setError("email already exists");
                                    }
                                    else{
                                        HelpUser user = new HelpUser(username,email, password,false);
                                        reference.child(username).setValue(user);

                                        Toast.makeText(getActivity(), "Sign-up successful!", Toast.LENGTH_SHORT).show();

                                        // Switch to the library fragment
                                        goBackToLoginFragment();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    HelpUser user = new HelpUser(username,email, password,false);
                                    reference.child(username).setValue(user);

                                    Toast.makeText(getActivity(), "Sign-up successful!", Toast.LENGTH_SHORT).show();

                                    // Switch to the library fragment
                                    goBackToLoginFragment();

                                }
                            });


                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

                // Store the user object in Firebase Realtime Database

                // Display a toast message to confirm sign-up

            }
        });

        buttonReturnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to the login fragment
                goBackToLoginFragment();
            }
        });

        return view;
    }

    private void goToLibraryFragment() {
        // Create an instance of LibraryFragment
        library libraryFragment = new library();

        // Replace the current fragment with LibraryFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, libraryFragment)
                .addToBackStack(null)
                .commit();
    }

    private void goBackToLoginFragment() {
        // Create an instance of LoginFragment
        login loginFragment = new login();

        // Replace the current fragment with LoginFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, loginFragment)
                .addToBackStack(null)
                .commit();
    }
}
