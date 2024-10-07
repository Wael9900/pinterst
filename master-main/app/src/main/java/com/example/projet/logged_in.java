package com.example.projet;


import static com.example.projet.HelpUser.log_out;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class logged_in extends Fragment {
    TextView t;

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logged_in, container, false);

        // Initialize EditText t
        t = view.findViewById(R.id.username_logged_in);

        FirebaseDatabase d = FirebaseDatabase.getInstance();
        DatabaseReference r = d.getReference("users");
        HelpUser.get_username_connected(r, new HelpUser.UsernameCallback() {
            @Override
            public void onUsernameReceived(String username) {
                t.setText(username);
            }
        });
        Button buttonLogout = view.findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the logout method
                logout();
            }
        });

        return view;
    }

    // Define the logout method
    public void logout() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();;
        DatabaseReference reference = database.getReference("users");
        log_out(reference);
        ProgressDialog p=new ProgressDialog(getContext());
        p.setMessage("Logging out...");
        p.show();
        new Handler().postDelayed(() -> {
            Toast.makeText(getContext(),"Log out successfully",Toast.LENGTH_LONG).show();
            login login = new login();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, login)
                    .addToBackStack(null)
                    .commit();
            p.dismiss();
        },1000);
    }
}
