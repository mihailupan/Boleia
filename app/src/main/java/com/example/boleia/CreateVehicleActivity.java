package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CreateVehicleActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //TODO
    String [] data;
    Button createTravelButton, openGalleryButtonVehicle, captureImageButtonVehicle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vehicle);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //TODO
        createTravelButton = findViewById(R.id.createTravelButton);
        createTravelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String txtToDB = getData();
                Toast.makeText(CreateVehicleActivity.this, txtToDB, Toast.LENGTH_LONG).show();
            }
        });
        //FALTA DAR ACAO AO BOTAO

        //TODO
        //TESTING
        Bundle bundle = getIntent().getExtras();
        data = bundle.getStringArray("data");



    }

    private String getData() {

        String str = "";
        for (int i = 0; i < data.length; i++)
        {
            str += data[i] +"\n";
        }
        return str;
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