package com.example.boleia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.analytics.FirebaseAnalytics;

public class DriverRegisterActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    Button forwardRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        forwardRegisterButton = findViewById(R.id.forwardRegisterDriverButton);

        forwardRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardRegistrationDriver();
            }
        });
    }

    public void forwardRegistrationDriver(){
        Intent intent = new Intent(this, VehicleRegisterAtivity.class);
        startActivity(intent);
    }

}