package com.example.women_safeguard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LocationsApi {
    @GET("fetch_locations.php") // Adjust the path if necessary
    Call<List<CustomLocation>> getLocations();
}
