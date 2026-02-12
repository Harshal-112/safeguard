package com.example.women_safeguard.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OSRMService {
    @GET("route/v1/driving/{coordinates}")
    Call<OSRMResponse> getRoute(
            @Path("coordinates") String coordinates,
            @Query("alternatives") boolean alternatives, // Fetch multiple routes
            @Query("overview") String overview // Use "full" to get complete route details
    );
}
