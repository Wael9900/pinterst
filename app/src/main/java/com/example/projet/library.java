package com.example.projet;

import static com.example.projet.HelpUser.getUrls;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class library extends Fragment {
    private ArrayList<String> photosLibraries = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view1);
        FirebaseDatabase d = FirebaseDatabase.getInstance();
        DatabaseReference r = d.getReference("users");
        getUrls(r, getContext(), new HelpUser.UrlsCallback() {
            @Override
            public void onUrlsReceived(List<String> urls)
            {
               photosLibraries.addAll(urls);
                Collections.reverse(photosLibraries);

                library_adaptor adapter = new library_adaptor(photosLibraries, getActivity());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        });
// Button to go to Library Local
        Button btnGoToLibraryLocal = rootView.findViewById(R.id.btnGoToLibraryLocal);
        btnGoToLibraryLocal.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), library_local.class);
            startActivity(intent);
        });


        return rootView;
    }
}
