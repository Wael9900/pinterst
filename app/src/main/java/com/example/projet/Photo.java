package com.example.projet;

import com.google.gson.annotations.SerializedName;

public class Photo {
    @SerializedName("id")
    private String id;

    @SerializedName("urls")
    private Urls urls;

    public String getId() {
        return id;
    }

    public Urls getUrls() {
        return urls;
    }
}
