package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class SearchActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Button nextButton;
    Spinner fromSpinner;
    Spinner toSpinner;
    String selectedFromCity;
    String selectedToCity;
    Button dateButton;
    int myDay, myMonth, myYear, myHour, myMinute;
    TextView dateTimeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //Initializes the spinners
        fromSpinner = findViewById(R.id.search_from_spinner);
        toSpinner = findViewById(R.id.search_to_spinner);

        //Saves the values from spinners
        selectFromSpinners();

        //Button to choose date and time
        dateButton = findViewById(R.id.search_date_picker_button);
        dateButton.setOnClickListener(this);

        //Next Button
        nextButton= findViewById(R.id.search_next_button);
        nextButton.setOnClickListener(this);

        //Shows the date and time in a textView
        dateTimeTextView = findViewById(R.id.search_date_time_text_view);

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
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myHour = hourOfDay;

                        calendar.set(Calendar.MINUTE, minute);
                        myMinute = minute;

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        simpleDateFormat.format(calendar.getTime());

                        myMonth += 1;

                        Toast.makeText(SearchActivity.this, "Ano: "+(myYear)+" Mes: "+(myMonth)+" Dia: "+(myDay), Toast.LENGTH_LONG).show();

                        //Show selected date and time
                        //dateTimeTextView.setText(simpleDateFormat.format(calendar.getTime()));
                        //dateTimeTextView.setVisibility(View.VISIBLE);

                        //Toast.makeText(CreateActivity.this, "Data: "+simpleDateFormat.format(calendar.getTime()), Toast.LENGTH_LONG).show();

                    }
                };


                String date = myDay+"-"+myMonth+"-"+myYear;
                dateTimeTextView.setText(date);
                dateTimeTextView.setVisibility(View.VISIBLE);

                new TimePickerDialog(SearchActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }


        };

        new DatePickerDialog(SearchActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    private void selectFromSpinners(){

        ArrayAdapter<CharSequence> fromAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Save selected value
                selectedFromCity = fromSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> toAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Save selected value
                selectedToCity = toSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sendData() {

        if (myYear == 0 && myMonth == 0 && myDay == 0 && myHour == 0 && myMinute == 0) {
            Toast.makeText(SearchActivity.this, "É necessário escolher uma data para a viagem!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(SearchActivity.this, SearchMapActivity.class);

        //Pass from and to
        intent.putExtra("fromCity", selectedFromCity);
        intent.putExtra("toCity", selectedToCity);

        //Pass date and time
        intent.putExtra("day", myDay);
        intent.putExtra("month", myMonth);
        intent.putExtra("year", myYear);
        intent.putExtra("hour", myHour);
        intent.putExtra("minute", myMinute);


        //Pass initial latitude and longitude
        double [] coordinates = new double[2];
        coordinates = getCityCoordinates(selectedFromCity);
        intent.putExtra("fromCityCoordinates", coordinates);
        startActivity(intent);
    }

    /**
     * @return Location (array of longitude and latitude) based on city (selected from) in spinner
     */
    private double[] getCityCoordinates(String fromCreate) {

        int locPosition = 0;
        String [] cities = {
                "Beja",
                "Evora",
                "Faro",
                "Lisboa",
                "Braga"
        };

        //Location of each city listed above
        double [][] citiesLocation = {
                {38.0173806,-7.8676554},
                {38.5743528,-7.9163379},
                {37.0177845,-7.9749516},
                {38.741348,-9.1694114},
                {41.5487301,-8.4389198}
        };

        for (int i = 0; i < cities.length; i++)
        {
            if(fromCreate.equals(cities[i]))
            {
                locPosition = i;
                break;
            }
        }

        return citiesLocation[locPosition];

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.searchNav) {
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


    @Override
    public void onClick(View v) {
        if (v.getId()== R.id.search_date_picker_button){
            showDateDialog();
        }
        if (v.getId()== R.id.search_next_button){
            sendData();
            Toast.makeText(SearchActivity.this, selectedToCity, Toast.LENGTH_SHORT).show();
        }
    }
}