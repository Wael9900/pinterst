package com.example.projet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class search extends Fragment {
    private static final String TAG = "search";
    private static final String BASE_URL = "https://api.unsplash.com/";
    private static final String ACCESS_KEY = "WEovlQvvD2spEdf1mUST8LXfrJ0MblbX7BQ5omjX_uE";
    private UnsplashApi unsplashApi;
    private RecyclerView recyclerView;
    private MyCustomAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create instance of Unsplash API
        unsplashApi = retrofit.create(UnsplashApi.class);

        // Get reference to SearchView
        SearchView searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                performSearch(query);
                Toast.makeText(getContext(), "after perform Search: " , Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query text change
                return false;
            }
        });

        // Initialize RecyclerView with LinearLayoutManager
        recyclerView = rootView.findViewById(R.id.searchCycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    private void performSearch(String query) {
        // Call the Unsplash API with the search query
        Call<SearchResponse> call = unsplashApi.searchPhotos(query, ACCESS_KEY);
         call.enqueue(new Callback<SearchResponse>() {

            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Handle successful response
                    List<Photo> photos = response.body().getResults();
                    // Create and set adapter for RecyclerView
                    adapter = new MyCustomAdapter(getContext(), photos);
                    recyclerView.setAdapter(adapter);
                } else {

                    // Handle unsuccessful response or null response body
                    String errorMessage = response.message() != null ? response.message() : "Unknown error";
                    Log.e(TAG, "Unsuccessful response: " + errorMessage);
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {

                // Handle failure
                Log.e(TAG, "Failure: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
