package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class SearchDetailActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    TextView fromTextView, toTextView, dateTextView, timeTextView, driverNameTextView, driverPhoneTextView,
             driverEmailTextView, vehicleBrandTextView, vehicleModelTextView, vehicleLicensePlateTextView;
    ImageView driverPhotoImageView, vehiclePhotoImageView;
    private StorageReference storageReference;
    Travel travel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);

        //References
        storageReference = FirebaseStorage.getInstance().getReference();

        //Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //TextView
        fromTextView = findViewById(R.id.search_detail_from_text_view);
        toTextView = findViewById(R.id.search_detail_to_text_view);
        dateTextView = findViewById(R.id.search_detail_date_text_view);
        timeTextView = findViewById(R.id.search_detail_time_text_view);
        driverNameTextView =  findViewById(R.id.search_detail_driver_name_text_view);
        driverPhoneTextView =  findViewById(R.id.search_detail_driver_phone_text_view);
        driverPhoneTextView.setOnClickListener(this);

        driverEmailTextView = findViewById(R.id.search_detail_driver_email_text_view);
        vehicleBrandTextView = findViewById(R.id.search_detail_vehicle_brand_text_view);
        vehicleModelTextView = findViewById(R.id.search_detail_vehicle_model_text_view);
        vehicleLicensePlateTextView = findViewById(R.id.search_detail_vehicle_license_plate_text_view);


        //ImageView
        driverPhotoImageView = findViewById(R.id.search_detail_driver_photo);
        vehiclePhotoImageView = findViewById(R.id.search_detail_vehicle_photo);


        //Get travel object using Gson library
        Gson gson = new Gson();
        travel = gson.fromJson(getIntent().getStringExtra("myjson"), Travel.class);

        //Set values
        this.setTextViewValues(travel);
        this.setImageViewValues(travel);

    }

    /**
     * This function will set the photos of the driver and the vehicle on each imageView
     * @param travel Travel object that contains the travel information
     */
    private void setImageViewValues(Travel travel) {

        //DriverPhoto
        driverPhoto(travel);

        //Vehicle
        vehiclePhoto(travel);
    }

    /**
     * Set vehicle photo
     * @param travel Travel object
     */
    private void vehiclePhoto(Travel travel) {
        storageReference.child(travel.getUserId() + "/" +travel.getVehiclePhotoName()).getDownloadUrl()
                .addOnSuccessListener(uri -> {
            // Got the download URL for 'userID/profile.png', set profile picture
            Picasso.get().load(uri).into(vehiclePhotoImageView);

        }).addOnFailureListener(exception -> {
            // Handle any errors
            Toast.makeText(SearchDetailActivity.this, R.string.car_photo_load_error, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Set driver photo
     * @param travel Travel object
     */
    private void driverPhoto(Travel travel) {
        storageReference.child(travel.getUserId() + "/" + "profile").getDownloadUrl()
                .addOnSuccessListener(uri -> {
            // Got the download URL for 'userID/profile.png', set profile picture
            Picasso.get().load(uri).into(driverPhotoImageView);

        }).addOnFailureListener(exception -> {
            // Handle any errors
            Toast.makeText(SearchDetailActivity.this, R.string.driver_photo_load_error, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * This function will set the information of the travel on each textView
     * @param travel Travel object that contains the travel information
     */
    private void setTextViewValues(Travel travel) {
        //Set all textViews
        fromTextView.setText(travel.getFrom());
        toTextView.setText(travel.getTo());
        dateTextView.setText(travel.getDate());
        timeTextView.setText(travel.getTime());
        driverNameTextView.setText(travel.getName());
        driverPhoneTextView.setText(travel.getPhone());
        driverEmailTextView.setText(travel.getEmail());
        vehicleBrandTextView.setText(travel.getVehicleBrand());
        vehicleModelTextView.setText(travel.getVehicleModel());
        vehicleLicensePlateTextView.setText(travel.getVehicleLicensePlate());
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
            overridePendingTransition(0, 0);
            return true;

        }
        else
        {
            if (item.getItemId() == R.id.createNav) {

                startActivity(new Intent(this, CreateActivity.class));
                overridePendingTransition(0, 0);
                return true;

            }
            else
            {
                if (item.getItemId() == R.id.travelsNav) {

                    startActivity(new Intent(this, TravelsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;

                }
                else
                {
                    if (item.getItemId() == R.id.profileNav) {

                        startActivity(new Intent(this, ProfileActivity.class));
                        overridePendingTransition(0, 0);
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

        if(v.getId() == R.id.search_detail_driver_phone_text_view)
        {
            //Open phone to call number
            if(isPermissionGranted()){
                call_action();
            }
        }
    }

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                call_action();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Start activity to call the driver number
     */
    public void call_action(){
        String phnum = driverPhoneTextView.getText().toString();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phnum));
        startActivity(callIntent);
    }
}