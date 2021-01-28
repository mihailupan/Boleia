package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CreateVehicleActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vehicle);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Button createTravel = findViewById(R.id.createTravelButton);
        //FALTA DAR ACAO AO BOTAO

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