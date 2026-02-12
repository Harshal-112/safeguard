package com.example.women_safeguard.network;

import java.util.List;

public class OSRMResponse {
    public List<Route> routes;

    public static class Route {
        public String geometry; // Encoded polyline for each route
    }
}