package com.example.boleia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CreateMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Initialize variable
    GoogleMap gMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);

        //Obtain the SupportMapFragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Assign variable
        gMap = googleMap;

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Creating Marker
                MarkerOptions markerOptions = new MarkerOptions();

                //Set Marker Position
                markerOptions.position(latLng);

                //Set Latitude and Longitude on Marker
                markerOptions.title(latLng.latitude+" : "+latLng.longitude);

                //Clear the previously click position
                gMap.clear();

                //Zoom the marker
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                //Add marker on map
                gMap.addMarker(markerOptions);
            }
        });
    }
}


