package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SearchMapActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    int day, month, year, hour, minute;
    String fromCity, toCity;
    double [] fromCitycoordinates = new double[2];
    LatLng latLngfromCity;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private StorageReference storageReference;
    private String userID;
    String date;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map);

        //get information from SearchActivity
        getInfo();

        //Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //Obtain the SupportMapFragment
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        //Initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        //Check permission for the map
        checkPermissions();

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = mAuth.getCurrentUser().getUid();
    }

    private void getInfo() {

        Bundle bundle = getIntent().getExtras();
        this.fromCity = bundle.getString("fromCity");
        this.toCity = bundle.getString("toCity");
        this.day = bundle.getInt("day");
        this.month = bundle.getInt("month");
        this.year = bundle.getInt("year");
        this.hour = bundle.getInt("hour");
        this.minute = bundle.getInt("minute");
        this.fromCitycoordinates = bundle.getDoubleArray("fromCityCoordinates");
        this.date = day+"-"+month+"-"+year;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission granted
            //Call method
            getCurrentLocation();
        }else {
            //When permission is not granted
            //Request permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check condition
        if(requestCode == 100 && (grantResults.length > 0  && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED))){
            //When permission are granted
            getCurrentLocation();
        }else {
            //When permission is denied
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        //Initialize location manager
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        //Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            //When location service is enable
            //Get last location
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Initialize location
                    Location location = task.getResult();

                    //Check condition

                    if(location != null){
                        //When location is not null
                        getInfoFromFirestore(location);

                    }else {
                        //When location result is null
                        //Initialize location request
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        //Initialize location callback

                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                //Initialize location
                                Location location1 = locationResult.getLastLocation();

                                getInfoFromFirestore(location1);

                            }
                        };
                        //Request location updates
                        client.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }
                }
            });
        }else {
            //When location service is not enable
            //Open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    private void getInfoFromFirestore(Location location){

        List<Travel> travelList = new ArrayList<>();

        mStore.collection("travels")
                .whereEqualTo("from", fromCity).whereEqualTo("to",toCity).whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Travel travel = new Travel(document.getString("userID"),document.getString("name"),document.getString("email"),
                                        document.getString("phone"),document.getString("from"),document.getString("to")
                                        ,document.getString("date"),document.getString("time"),document.getString("meetingPointLat"),
                                        document.getString("meetingPointLng"),document.getString("vehicleBrand"),document.getString("vehicleModel"),
                                        document.getString("vehicleLicensePlate"),document.getString("vehiclePhotoName"));

                                travelList.add(travel);



                                //Toast.makeText(SearchMapActivity.this, name, Toast.LENGTH_SHORT).show();
                            }
                            map(location, travelList);
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }


    private void map(Location location, List<Travel> travelList){

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {



                for (int i=0; i< travelList.size(); i++){
                    double latMeet = Double.parseDouble(travelList.get(i).getMeetingPointLat());
                    double lngMeet = Double.parseDouble(travelList.get(i).getMeetingPointLng());

                    Toast.makeText(SearchMapActivity.this, i+"_"+latMeet +"___" + lngMeet, Toast.LENGTH_SHORT).show();

                    LatLng meet = new LatLng(latMeet,lngMeet);
                    googleMap.addMarker(new MarkerOptions().position(meet).title(travelList.get(i).getFrom()));
                }


                //User current location
                double currentLocationLat =  location.getLatitude();
                double currentLocationLng = location.getLongitude();

                LatLng latLngCurrentLocation = new LatLng(currentLocationLat,currentLocationLng);

                //Location of the "from" city
                location.setLatitude(fromCitycoordinates[0]);
                location.setLongitude(fromCitycoordinates[1]);
                latLngfromCity = new LatLng(location.getLatitude(), location.getLongitude());

                //Add Marker in the "from city"
                googleMap.addMarker(new MarkerOptions()
                        .position(latLngCurrentLocation).title("Localização atual!")
                        .icon(bitmapDescriptorFromVector(getApplicationContext(),
                                R.drawable.ic_current_location)));

                //Zoom map to the "from" city
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngfromCity, 15));


                /*googleMap.addMarker(new MarkerOptions().position(meet));
                googleMap.addMarker(new MarkerOptions().position(meet1));*/


            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.searchNav) {

            startActivity(new Intent(this, SearchActivity.class));
            overridePendingTransition(0,0);
            return true;

        }
        if (item.getItemId() == R.id.createNav) {

            startActivity(new Intent(this, CreateActivity.class));
            overridePendingTransition(0,0);
            return true;

        }
        if (item.getItemId() == R.id.travelsNav) {

            startActivity(new Intent(this, TravelsActivity.class));
            overridePendingTransition(0,0);
            return true;

        }
        if (item.getItemId() == R.id.profileNav) {

            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0,0);
            return true;
        }
        return false;
    }
}