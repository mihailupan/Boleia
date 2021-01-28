package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button button;
    int day, month, year, hour, minute;
    int myDay, myMonth, myYear, myHour, myMinute;
    TextView showDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        //SpinneFrom
        Spinner createCitiesSpinnerFrom = findViewById(R.id.createCitiesSpinnerFrom);
        ArrayAdapter<CharSequence> createCitiesSpinnerFromAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        createCitiesSpinnerFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createCitiesSpinnerFrom.setAdapter(createCitiesSpinnerFromAdapter);
        createCitiesSpinnerFrom.setOnItemSelectedListener(this);

        //SpinnerTo
        Spinner createCitiesSpinnerTo = findViewById(R.id.createCitiesSpinnerTo);
        ArrayAdapter<CharSequence> createCitiesSpinnerToAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        createCitiesSpinnerToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createCitiesSpinnerTo.setAdapter(createCitiesSpinnerToAdapter);
        createCitiesSpinnerTo.setOnItemSelectedListener(this);


        //Button to choose date
        button = findViewById(R.id.create_trip_date_picker);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //ShowDateTime textView
        showDateTime = findViewById(R.id.showCreateDateTextView);
    }


    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                myYear = year;

                calendar.set(Calendar.MONTH, month);
                myMonth = month;

                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                myDay = dayOfMonth;

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        myHour = hour;

                        calendar.set(Calendar.MINUTE, minute);
                        myMinute = minute;

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        simpleDateFormat.format(calendar.getTime());

                        myMonth+= 1;

                        Toast.makeText(CreateActivity.this, "Ano: "+(myYear)+" Mes: "+(myMonth)+" Dia: "+(myDay), Toast.LENGTH_LONG).show();

                        //Show selected date and time
                        showDateTime.setText(simpleDateFormat.format(calendar.getTime()));
                        showDateTime.setVisibility(View.VISIBLE);

                        //Toast.makeText(CreateActivity.this, "Data: "+simpleDateFormat.format(calendar.getTime()), Toast.LENGTH_LONG).show();

                    }
                };

                new TimePickerDialog(CreateActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(CreateActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.searchNav) {

            startActivity(new Intent(this, SearchActivity.class));
            overridePendingTransition(0,0);
            return true;

        }
        if (item.getItemId() == R.id.createNav) {

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

    @Override
    public void onClick(View v) {

    }


    //Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    //Spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}