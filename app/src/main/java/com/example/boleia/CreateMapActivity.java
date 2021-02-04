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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class CreateMapActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //Initialize variables
    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    Button next;
    LatLng latLng;
    String fromCreate, toCreate;
    int myDay, myMonth, myYear, myHour, myMinute;
    double [] initLocation = new double[2];

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        getBundleContent();

        //Obtain the SupportMapFragment
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        //Initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        //Next button to continue creating travel
        next = findViewById(R.id.create_map_chose_location_next_button);
        next.setVisibility(View.INVISIBLE); //Only after the user select a meeting point in the map, he can see the next button and click this button
        next.setOnClickListener(this);

        //Check permission and get current location
        checkPermissions();

    }

    /**
     * Function to get the content passed to the activity
     */
    private void getBundleContent() {
        Bundle bundle = getIntent().getExtras();
        fromCreate = bundle.getString("fromCreate");
        toCreate = bundle.getString("toCreate");

        myDay = bundle.getInt("myDay");
        myMonth = bundle.getInt("myMonth");
        myYear = bundle.getInt("myYear");
        myHour = bundle.getInt("myHour");
        myMinute = bundle.getInt("myMinute");
        initLocation = bundle.getDoubleArray("location");
    }

    /**
     * Check if app has permission to get user location
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission is granted
                //Call method to get current location and open map to select meeting point
                getCurrentLocation();
        }else {
            //When permission is not granted
                //Request permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    /**
     * Function to verify the request permission result
     * @param requestCode Request code
     * @param permissions Array of permission
     * @param grantResults Array of grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check condition
        if(requestCode == 100 && (grantResults.length > 0  && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED))){
            //When permission is granted
                //Call method to get current location and open map to select meeting point
                getCurrentLocation();
        }else {
            //When permission is denied
                Toast.makeText(this, "Permissão negada!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get current location and call function to open map
     */
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        //Initialize location manager
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        //Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            //When location service is enable
            //Get last location
            client.getLastLocation().addOnCompleteListener(task -> {
                //Initialize location
                Location location = task.getResult();

                //Check condition

                if(location != null){
                    //When location is not null
                    map(location);

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

                            map(location1);

                        }
                    };
                    //Request location updates
                    client.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                }
            });
        }else {
            //When location service is not enable
                //Open location setting
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }


    /**
     * Open and show map
     * @param location Location to zoom when the map fragment opens
     */
    private void map(Location location){

        supportMapFragment.getMapAsync(googleMap -> {
            //CurrentLocation
            double currentLocationLat =  location.getLatitude();
            double currentLocationLng = location.getLongitude();

            //Location of the "from" city
            location.setLatitude(initLocation[0]);
            location.setLongitude(initLocation[1]);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

            //Zoom map to the "from" city
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


            LatLng latLngCurrentLocation = new LatLng(currentLocationLat,currentLocationLng);


            //Add Marker in the "from city"
            googleMap.addMarker(new MarkerOptions()
                    .position(latLngCurrentLocation).title(getString(R.string.current_location))
                    .icon(bitmapDescriptorFromVector(getApplicationContext(),
                            R.drawable.ic_current_location)));


            googleMap.setOnMapClickListener(latLng2 -> {
                //Creating Marker
                MarkerOptions markerOptions = new MarkerOptions();

                //Set Marker Position
                markerOptions.position(latLng2);

                //Set Latitude and Longitude on Marker
                markerOptions.title(latLng2.latitude+" : "+latLng2.longitude);
                latLng = latLng2;

                //Clear the previously click position
                googleMap.clear();

                //Zoom the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 15));

                //Add marker on map
                googleMap.addMarker(markerOptions);

                //Set button visible so the user can continue to new activity
                next.setVisibility(View.VISIBLE);

            });

        });
    }

    /**
     * Function to put vector image in marker
     * @param context Application context
     * @param vectorResId Drawable id
     * @return Bitmap Image
     */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    /**
     * Function to get the item selected on the navigation item
     * @param item Item selected in the menuItem
     * @return Boolean value, will return true if any navigation item is clicked
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.searchNav) {

            startActivity(new Intent(this, SearchActivity.class));
            overridePendingTransition(0,0);
            return true;

        }
        else
        {
            if (item.getItemId() == R.id.createNav) {

                startActivity(new Intent(this, CreateActivity.class));
                overridePendingTransition(0,0);
                return true;

            }
            else{

                if (item.getItemId() == R.id.travelsNav) {

                    startActivity(new Intent(this, TravelsActivity.class));
                    overridePendingTransition(0,0);
                    return true;

                }
                else
                {
                    if (item.getItemId() == R.id.profileNav) {

                        startActivity(new Intent(this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    }
                }
            }
        }


        return false;
    }

    /**
     * Function to see which view was clicked and do something depending on the view clicked
     * @param v View selected
     */
    @Override
    public void onClick(View v) {

        //Next button to coontinue to new activity and pass data as an array
        if (v.getId() == R.id.create_map_chose_location_next_button) {

            String [] data = {
                    fromCreate,
                    toCreate,
                    String.valueOf(myDay),
                    String.valueOf(myMonth),
                    String.valueOf(myYear),
                    String.valueOf(myHour),
                    String.valueOf(myMinute),
                    String.valueOf(latLng.latitude),
                    String.valueOf(latLng.longitude)
            };

            Intent intent = new Intent(this, CreateVehicleActivity.class);
            intent.putExtra("data", data);
            startActivity(intent);

        }
    }
}


