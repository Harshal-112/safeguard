package com.example.women_safeguard;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DirectionsResponse {

    private List<Route> routes;

    public List<LatLng> getRoutePoints() {
        List<LatLng> points = new ArrayList<>();
        for (Route route : routes) {
            for (Leg leg : route.getLegs()) {
                points.addAll(leg.getPolyline().getPoints());
            }
        }
        return points;
    }

    public static class Route {
        private List<Leg> legs;

        public List<Leg> getLegs() {
            return legs;
        }
    }

    public static class Leg {
        private Polyline polyline;

        public Polyline getPolyline() {
            return polyline;
        }
    }

    public static class Polyline {
        private List<LatLng> points;

        public List<LatLng> getPoints() {
            return points;
        }
    }
}
