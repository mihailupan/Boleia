package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class SearchDetailActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    TextView fromTextView, toTextView, dateTextView, timeTextView, driverNameTextView, driverPhoneTextView,
             driverEmailTextView, vehicleBrandTextView, vehicleModelTextView, vehicleLicensePlateTextView;
    ImageView driverPhotoImageView, vehiclePhotoImageView;
    private StorageReference storageReference;

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
        driverEmailTextView = findViewById(R.id.search_detail_driver_email_text_view);
        vehicleBrandTextView = findViewById(R.id.search_detail_vehicle_brand_text_view);
        vehicleModelTextView = findViewById(R.id.search_detail_vehicle_model_text_view);
        vehicleLicensePlateTextView = findViewById(R.id.search_detail_vehicle_license_plate_text_view);


        //ImageView
        driverPhotoImageView = findViewById(R.id.search_detail_driver_photo);
        vehiclePhotoImageView = findViewById(R.id.search_detail_vehicle_photo);


        //Get travel object using Gson library
        Gson gson = new Gson();
        Travel travel = gson.fromJson(getIntent().getStringExtra("myjson"), Travel.class);

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

    private void vehiclePhoto(Travel travel) {
        storageReference.child(travel.getUserId() + "/" +travel.getVehiclePhotoName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'userID/profile.png', set profile picture
                //Toast.makeText(ProfileActivity.this, "Image Uri!"+uri, Toast.LENGTH_SHORT).show();;
                Picasso.get().load(uri).into(vehiclePhotoImageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(SearchDetailActivity.this, "Download Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void driverPhoto(Travel travel) {
        storageReference.child(travel.getUserId() + "/" + "profile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'userID/profile.png', set profile picture
                //Toast.makeText(ProfileActivity.this, "Image Uri!"+uri, Toast.LENGTH_SHORT).show();;
                Picasso.get().load(uri).into(driverPhotoImageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(SearchDetailActivity.this, "Download Failed!", Toast.LENGTH_SHORT).show();
            }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.searchNav) {

            startActivity(new Intent(this, SearchActivity.class));
            overridePendingTransition(0, 0);
            return true;

        }
        if (item.getItemId() == R.id.createNav) {

            startActivity(new Intent(this, CreateActivity.class));
            overridePendingTransition(0, 0);
            return true;

        }
        if (item.getItemId() == R.id.travelsNav) {

            startActivity(new Intent(this, TravelsActivity.class));
            overridePendingTransition(0, 0);
            return true;

        }
        if (item.getItemId() == R.id.profileNav) {

            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }
        return false;
    }
}