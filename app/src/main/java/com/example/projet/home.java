package com.example.projet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class home extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the "Let's Start" button
        Button btnStart = rootView.findViewById(R.id.btnStart);
        btnStart.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500));



        // Set click listener for the button
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the search fragment
                navigateToSearch();
            }
        });

        return rootView;
    }

    // Method to navigate to the search fragment
    private void navigateToSearch() {
        // Create an intent to navigate to the search fragment
        Intent intent = new Intent(getActivity(), search.class);
        startActivity(intent);
    }
}
