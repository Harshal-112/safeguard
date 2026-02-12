package com.example.women_safeguard;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.women_safeguard.database.Report;
import com.example.women_safeguard.FakeRingingActivity;
import com.example.women_safeguard.GPSTracker;
import com.example.women_safeguard.PredefinedLocations;
import com.example.women_safeguard.network.OSRMResponse;
import com.example.women_safeguard.network.OSRMService;
import com.example.women_safeguard.database.User;
import com.example.women_safeguard.watch.SosListenerService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.maps.android.heatmaps.HeatmapTileProvider;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    String cityName;
    String str;
    String location, latlong;
    String city;
    String uid, busno;
    Button stopButton, emergencyButton;
    List<Address> addresses;
    Button ybtn, obtn, rbtn;
    double lat, lng;
    String[] sap;

    private GoogleMap googleMap;
    private OSRMService osrmService;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LatLng currentLatLng;
    private boolean notificationSent = false;
    private LatLng currentDestination = null;
    private Polyline currentRoute;
    private HeatmapTileProvider heatmapProvider;
    private TileOverlay heatmapOverlay;
    private static final List<LatLng> CRIME_SPOTS = new ArrayList<>();
    private Marker destinationMarker;

    private Map<String, Integer> incidentFrequency = new HashMap<>();
    private List<Circle> crimeSpotCircles = new ArrayList<>();
    private Marker userMarker;
    private List<LatLng> currentRoutePoints;
    private final Map<LatLng, String> REPORT_DESCRIPTIONS = new HashMap<>();

    private static final double ROUTE_TRIM_THRESHOLD = 0.00002;

    static {

        CRIME_SPOTS.add(new LatLng(20.458398, 74.182562)); // Deola
        CRIME_SPOTS.add(new LatLng(20.201377, 73.830341)); // Dindori
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://router.project-osrm.org/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        osrmService = retrofit.create(OSRMService.class);

        checkAndRequestPermission();

        // Use Firebase to retrieve user contact information
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    sap = new String[]{user.contactNumber, user.emergency1, user.emergency2, user.emergency3};
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Dashboard", "Failed to read user data", databaseError.toException());
            }
        });

        // Fetch reported incidents
        fetchReportedIncidents();

        loc();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GPSTracker gpsTracker = new GPSTracker(this);

        // Set up AutoCompleteTextView with predefined locations
        AutoCompleteTextView destinationInput = findViewById(R.id.destinationInput);
        List<String> locationNames = PredefinedLocations.getLocationNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locationNames);
        destinationInput.setAdapter(adapter);

        Button routeButton = findViewById(R.id.routeButton);
        routeButton.setOnClickListener(v -> {
            String destinationName = destinationInput.getText().toString();

            if (currentLatLng == null) {
                Toast.makeText(Dashboard.this, "Fetching location... Please wait.", Toast.LENGTH_SHORT).show();
                return;
            }

            String closestLocation = PredefinedLocations.getClosestMatchingLocation(destinationName);
            if (closestLocation != null) {
                currentDestination = PredefinedLocations.getLocation(closestLocation);
                requestRoute(currentLatLng, currentDestination);
            } else {
                searchLocation(destinationName);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (sap != null && sap.length > 0) {
                String message = " My Location is: http://maps.google.com/maps?q=loc:" + location;
                for (String contact : sap) {
                    if (contact != null && !contact.isEmpty()) {
                        sendSMS(contact, message);
                    }
                }
            } else {
                Toast.makeText(Dashboard.this, "No emergency contacts found!", Toast.LENGTH_SHORT).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Start location updates
        startLocationUpdates();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            Intent in2 = new Intent(getApplicationContext(), VoiceEmergency.class);
            startActivity(in2);
        } else if (id == R.id.nav_slideshow) {
            Intent in4 = new Intent(getApplicationContext(), BatterySettingsActivity.class);
            startActivity(in4);
        } else if (id == R.id.nav_manage) {
            Intent in4 = new Intent(getApplicationContext(), FakeRingingActivity.class);
            startActivity(in4);
        } else if (id == R.id.nav_safemap) {
            Intent in4 = new Intent(getApplicationContext(), SafeMap.class);
            startActivity(in4);
        } else if (id == R.id.nav_send) {
            Intent in = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_safety) {
            Intent in = new Intent(getApplicationContext(), SafetyCheckActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_user) {
            Intent in = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(in);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            Intent in2 = new Intent(getApplicationContext(), VoiceEmergency.class);
            startActivity(in2);
        } else if (id == R.id.nav_slideshow) {
            Intent in4 = new Intent(getApplicationContext(), BatterySettingsActivity.class);
            startActivity(in4);
        } else if (id == R.id.nav_manage) {
            Intent in4 = new Intent(getApplicationContext(), FakeRingingActivity.class);
            startActivity(in4);
        } else if (id == R.id.nav_safemap) {
            Intent in4 = new Intent(getApplicationContext(), SafeMap.class);
            startActivity(in4);
        } else if (id == R.id.nav_send) {
            Intent in = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_safety) {
            Intent in = new Intent(getApplicationContext(), SafetyCheckActivity.class);
            startActivity(in);
        }  else if (id == R.id.nav_user) {
            Intent in = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(in);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loc() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        LocationListener ll = new mylocationlistener();
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, ll);
        if (loc != null) {
            lat = loc.getLatitude();
            lng = loc.getLongitude();

            location = lat + "," + lng;

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(lat, lng, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert addresses != null;
            cityName = addresses.get(0).getAddressLine(0);
        }
    }

    private class mylocationlistener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("LOCATION CHANGED", location.getLatitude() + "");
                Log.d("LOCATION CHANGED", location.getLongitude() + "");
                str = "\n CurrentLocation: " + "\n Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude();

                String locationStr = location.getLatitude() + "," + location.getLongitude();
                Geocoder geocoder = new Geocoder(Dashboard.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                    assert addresses != null;
                    cityName = addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "SMS failed to send", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(5000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(10)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // Update user marker without refreshing the map
                    updateUserLocation(newLatLng);

                    // Only update the route if the user has moved significantly
                    if (currentLatLng == null || distanceBetween(currentLatLng, newLatLng) > 0.0001) {
                        currentLatLng = newLatLng;

                        if (currentDestination != null) {
                            // Always request a new route as the user moves
                            requestRoute(currentLatLng, currentDestination);
                        }
                    }

                    checkNearbyCrimeSpot(currentLatLng);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    private void trimRouteBasedOnUserLocation(LatLng userLocation) {
        if (currentRoutePoints == null || currentRoutePoints.isEmpty()) {
            return;
        }

        // Find the closest point on the route to the user's location
        int closestPointIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < currentRoutePoints.size(); i++) {
            double distance = distanceBetween(userLocation, currentRoutePoints.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                closestPointIndex = i;
            }
        }

        // If user is close enough to the route, trim all points before the closest point
        if (minDistance < ROUTE_TRIM_THRESHOLD) {
            List<LatLng> trimmedRoute = new ArrayList<>(
                    currentRoutePoints.subList(closestPointIndex, currentRoutePoints.size())
            );

            // Update the route on the map
            drawTrimmedRoute(trimmedRoute, currentRoute != null ? currentRoute.getColor() : 0xFF00FF00);
        }
    }
    private void drawTrimmedRoute(List<LatLng> latLngs, int color) {
        runOnUiThread(() -> {
            // Remove existing route
            if (currentRoute != null) {
                currentRoute.remove();
            }

            // Draw new trimmed route
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(latLngs)
                    .color(color)
                    .width(8f);
            currentRoute = googleMap.addPolyline(polylineOptions);

            // Store the current route points
            currentRoutePoints = latLngs;
        });
    }

    private void requestRoute(LatLng startPoint, LatLng endPoint) {
        String coordinates = startPoint.longitude + "," + startPoint.latitude + ";"
                + endPoint.longitude + "," + endPoint.latitude;

        retrofit2.Call<OSRMResponse> call = osrmService.getRoute(coordinates, true, "full");
        call.enqueue(new retrofit2.Callback<OSRMResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<OSRMResponse> call, @NonNull retrofit2.Response<OSRMResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<List<LatLng>> safeRoutes = new ArrayList<>();
                    List<List<LatLng>> unsafeRoutes = new ArrayList<>();

                    // Sort routes into Safe and Unsafe
                    for (OSRMResponse.Route route : response.body().routes) {
                        List<LatLng> latLngs = decodePolyline(route.geometry);
                        if (routePassesCrimeSpot(latLngs)) {
                            unsafeRoutes.add(latLngs);
                        } else {
                            safeRoutes.add(latLngs);
                        }
                    }

                    runOnUiThread(() -> {
                        // Store and draw the appropriate route
                        if (!safeRoutes.isEmpty()) {
                            currentRoutePoints = safeRoutes.get(0);
                            drawTrimmedRoute(currentRoutePoints, 0xFF00FF00);
                        } else if (!unsafeRoutes.isEmpty()) {
                            currentRoutePoints = unsafeRoutes.get(0);
                            drawTrimmedRoute(currentRoutePoints, 0xFFFF0000);
                        }

                        // Draw red circles around crime spots
                        drawCrimeSpotCircles();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<OSRMResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(Dashboard.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private boolean routePassesCrimeSpot(List<LatLng> routePoints) {
        for (LatLng crimeSpot : CRIME_SPOTS) {
            for (LatLng routePoint : routePoints) {
                if (distanceBetween(crimeSpot, routePoint) < 0.002) { // 200 meters
                    return true; // Route is unsafe
                }
            }
        }
        return false; // Route is safe
    }

    private void sendUnsafeRouteNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "safeRouteChannel")
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("⚠ Unsafe Area Nearby")
                .setContentText("You are within 200 meters of a crime-prone area. Stay alert!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "safeRouteChannel",
                    "Safe Route Alerts",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, builder.build());
    }

    private double distanceBetween(LatLng p1, LatLng p2) {
        return Math.sqrt(Math.pow(p1.latitude - p2.latitude, 2) + Math.pow(p1.longitude - p2.longitude, 2));
    }

    private void searchLocation(String locationName) {

        try {
            // Encode spaces and special characters
            String encodedName = java.net.URLEncoder.encode(locationName, "UTF-8");

            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedName;

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();

                        // Required by OpenStreetMap Nominatim
                        Request request = original.newBuilder()
                                .header("User-Agent", "WomenSafeguardApp/1.0 (contact: your_email@example.com)")
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(Dashboard.this, "Failed to fetch location", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();

                        try {
                            JSONArray jsonArray = new JSONArray(jsonResponse);

                            if (jsonArray.length() > 0) {
                                JSONObject firstResult = jsonArray.getJSONObject(0);

                                double lat = firstResult.getDouble("lat");
                                double lon = firstResult.getDouble("lon");

                                runOnUiThread(() -> {
                                    currentDestination = new LatLng(lat, lon);
                                    requestRoute(currentLatLng, currentDestination);
                                    Toast.makeText(Dashboard.this, "Navigating to: " + locationName, Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                runOnUiThread(() ->
                                        Toast.makeText(Dashboard.this, "No results found!", Toast.LENGTH_SHORT).show()
                                );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Search failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchReportedIncidents() {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        reportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CRIME_SPOTS.clear(); // Clear existing spots

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);
                    if (report != null && report.getLocation() != null) {
                        // Only display approved reports
                        if (report.getStatus() != null && report.getStatus().equals("approved")) {
                            try {
                                String location = report.getLocation();
                                LatLng reportLocation;

                                if (location.startsWith("Lat: ")) {
                                    // Parse "Lat: X.XXX, Lng: Y.YYY" format
                                    String cleanLoc = location.replace("Lat: ", "").replace("Lng: ", "");
                                    String[] parts = cleanLoc.split(", ");
                                    double latitude = Double.parseDouble(parts[0]);
                                    double longitude = Double.parseDouble(parts[1]);
                                    reportLocation = new LatLng(latitude, longitude);
                                } else if (location.contains(",")) {
                                    // Parse "X.XXX,Y.YYY" format
                                    String[] latLng = location.split(",");
                                    double latitude = Double.parseDouble(latLng[0].trim());
                                    double longitude = Double.parseDouble(latLng[1].trim());
                                    reportLocation = new LatLng(latitude, longitude);
                                } else {
                                    // Skip invalid formats
                                    continue;
                                }

                                CRIME_SPOTS.add(reportLocation);
                            } catch (Exception e) {
                                Log.e("Dashboard", "Invalid location format: " + report.getLocation(), e);
                            }
                        }
                    }
                }

                // Add predefined crime spots if we don't have enough data
                if (CRIME_SPOTS.size() < 3) {
                    List<LatLng> defaultSpots = PredefinedLocations.getDefaultCrimeSpots();
                    for (LatLng spot : defaultSpots) {
                        if (!CRIME_SPOTS.contains(spot)) {
                            CRIME_SPOTS.add(spot);
                        }
                    }
                }

                // Draw circles for all crime spots
                drawCrimeSpotCircles();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Dashboard", "Failed to fetch reports: " + databaseError.getMessage());
            }
        });
    }

    private void updateUserLocation(LatLng newLocation) {
        runOnUiThread(() -> {
            // Only handle camera movement and destination marker
            if (currentLatLng == null) {
                // First time location update - animate camera
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));
            }

            currentLatLng = newLocation;

            if (currentDestination != null && destinationMarker == null) {
                destinationMarker = googleMap.addMarker(new MarkerOptions()
                        .position(currentDestination)
                        .title("Destination"));
            }
        });
    }

    private void checkNearbyCrimeSpot(LatLng userLocation) {
        for (LatLng crimeSpot : CRIME_SPOTS) {
            if (distanceBetween(userLocation, crimeSpot) < 0.002) { // 200 meters
                if (!notificationSent) {
                    sendUnsafeRouteNotification();
                    notificationSent = true;

                    // Send SMS to emergency contacts
                    if (sap != null && sap.length > 0) {
                        String message = "I am within 200 meters of a crime-prone area. My Location is: http://maps.google.com/maps?q=loc:" + userLocation.latitude + "," + userLocation.longitude;
                        for (String contact : sap) {
                            if (contact != null && !contact.isEmpty()) {
                                sendSMS(contact, message);
                            }
                        }
                    } else {
                        Toast.makeText(Dashboard.this, "No emergency contacts found!", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
        }
    }

    private void drawCrimeSpotCircles() {
        if (googleMap == null) return;

        // Clear existing circles
        for (Circle circle : crimeSpotCircles) {
            circle.remove();
        }
        crimeSpotCircles.clear();

        // Draw new circles
        for (LatLng crimeSpot : CRIME_SPOTS) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(crimeSpot)
                    .radius(200) // 200 meters
                    .fillColor(Color.argb(150, 255, 0, 0)) // Semi-transparent red fill
                    .strokeWidth(0); // No border

            Circle circle = googleMap.addCircle(circleOptions);
            crimeSpotCircles.add(circle);
        }
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> polyline = new ArrayList<>();
        int index = 0, lat = 0, lng = 0;

        while (index < encoded.length()) {
            int shift = 0, result = 0, b;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            polyline.add(new LatLng(lat / 1E5, lng / 1E5));
        }
        return polyline;
    }

    private void drawRoute(List<LatLng> latLngs, int color) {
        // Remove the current route if it exists
        if (currentRoute != null) {
            currentRoute.remove();
        }
        List<LatLng> filteredLatLngs = new ArrayList<>();
        boolean startAdding = false;

        for (LatLng point : latLngs) {
            if (startAdding || distanceBetween(currentLatLng, point) < 0.0001) {
                startAdding = true;
                filteredLatLngs.add(point);
            }
        }

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(latLngs)
                .color(color)
                .width(8f);
        currentRoute = googleMap.addPolyline(polylineOptions);

        // Make sure destination marker is showing
        if (currentDestination != null) {
            if (destinationMarker != null) {
                destinationMarker.remove();
            }
            destinationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(currentDestination)
                    .title("Destination"));
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        // Fetch incidents and draw circles
        fetchReportedIncidents();

        // Move the camera to the user's current location when available
        if (currentLatLng != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        } else {
            // Default to Nashik center if user location not available
            LatLng nashikCenter = new LatLng(20.0011, 73.7900);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nashikCenter, 13));
        }
    }}