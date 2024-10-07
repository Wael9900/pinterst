package com.example.projet;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
    @SerializedName("results")
    private List<Photo> results;

    public List<Photo> getResults() {
        return results;
    }

    // You may add more fields if needed
}
