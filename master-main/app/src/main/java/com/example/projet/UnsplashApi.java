package com.example.projet;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UnsplashApi {
    @GET("/search/photos")
    Call<SearchResponse> searchPhotos(
            @Query("query") String query,
            @Query("client_id") String clientId
    );
}
