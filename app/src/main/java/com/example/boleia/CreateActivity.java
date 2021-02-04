package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

public class CreateActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    String fromCreate, toCreate;
    Spinner createCitiesSpinnerFrom, createCitiesSpinnerTo;
    Button chooseDateBtn, advanceBtn;
    String defaultSpinnerValue;
    int myDay, myMonth, myYear, myHour, myMinute;
    TextView showDateTime;
    private int fromCreatePos, toCreatePos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        defaultSpinnerValue = String.valueOf(R.string.defaul_spinner_value);

        //Position of item spinner
        fromCreatePos = 0;
        toCreatePos = 0;

        //SpinnerFrom
        createCitiesSpinnerFrom = findViewById(R.id.createCitiesSpinnerFrom);
        ArrayAdapter<CharSequence> createCitiesSpinnerFromAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        createCitiesSpinnerFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createCitiesSpinnerFrom.setAdapter(createCitiesSpinnerFromAdapter);
        createCitiesSpinnerFrom.setOnItemSelectedListener(this);


        //SpinnerTo
        createCitiesSpinnerTo = findViewById(R.id.createCitiesSpinnerTo);
        ArrayAdapter<CharSequence> createCitiesSpinnerToAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        createCitiesSpinnerToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createCitiesSpinnerTo.setAdapter(createCitiesSpinnerToAdapter);
        createCitiesSpinnerTo.setOnItemSelectedListener(this);


        //Button to choose date
        chooseDateBtn = findViewById(R.id.create_trip_date_picker);
        chooseDateBtn.setOnClickListener(this);

        //Button to go to another activity and continue creating travel
        advanceBtn = findViewById(R.id.advance_create_trip);
        advanceBtn.setOnClickListener(this);

        //Bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //ShowDateTime textView
        showDateTime = findViewById(R.id.showCreateDateTextView);
    }

    /**
     * Function that will start new activity
     */
    private void goToActivity() {

        Date date= new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        if (dateIsNotValid(currentYear, currentMonth, currentDay)) return;
        //Check if any city was selected using selected pos and user didn't selected same city
        if((fromCreatePos == 0 || toCreatePos == 0) || fromCreate.equals(toCreate))
        {
            Toast.makeText(this, "Por favor, selecione uma cidade válida!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(CreateActivity.this, CreateMapActivity.class);

        passValuesToActivity(intent);

        startActivity(intent);
    }

    /**
     * Function to pass values to another activity
     * @param intent Contains the activity which will receive the data
     */
    private void passValuesToActivity(Intent intent) {
        //Pass from and to
        intent.putExtra("fromCreate", fromCreate);
        intent.putExtra("toCreate", toCreate);

        //Pass date and time
        intent.putExtra("myDay", myDay);
        intent.putExtra("myMonth", myMonth);
        intent.putExtra("myYear", myYear);
        intent.putExtra("myHour", myHour);
        intent.putExtra("myMinute", myMinute);

        //Pass initial latitude and longitude
        double [] location = getInitialLoc(fromCreate);
        intent.putExtra("location", location);
    }

    /**
     * Validate the date the user selected, check if user selected any date and if the date wasn't in the past
     * @param currentYear Actual year when running the app
     * @param currentMonth Actual month when running the app
     * @param currentDay Actual day when running the app
     * @return Boolean value, returns false if date is not valid
     */
    private boolean dateIsNotValid(int currentYear, int currentMonth, int currentDay) {
        //Check if date was selected
        if (myYear == 0 && myMonth == 0 && myDay == 0 && myHour == 0 && myMinute == 0) {
            Toast.makeText(CreateActivity.this, "É necessário escolher uma data para a viagem!", Toast.LENGTH_LONG).show();
            return true;
        }

        //Check if data selected is valid
        if (myYear < currentYear || myMonth < currentMonth || myDay < currentDay)
        {Toast.makeText(CreateActivity.this, "Data inválida, por favor selecione nova data!", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }


    /**
     * @return Location (array of longitude and latitude) based on city (selected from) in spinner
     */
    private double[] getInitialLoc(String fromCreate) {

        int locPosition = 0;

        //Array of each city name
        String [] cities = {
                "Beja",
                "Évora",
                "Faro",
                "Lisboa",
                "Braga"
        };

        //Location of each city listed above
        double [][] citiesLocation = {
                {38.0173806,-7.8676554},
                {38.5743528,-7.9163379},
                {37.019395,-7.930615},
                {38.721493,-9.139282},
                {41.545672,-8.426505}
        };

        //For cycle to get the location of the city selected on from spinner
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


    /**
     * Function to show the user the date dialog
     */
    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            myYear = year;

            calendar.set(Calendar.MONTH, month);
            myMonth = month;

            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            myDay = dayOfMonth;

            TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myHour = hourOfDay;

                calendar.set(Calendar.MINUTE, minute);
                myMinute = minute;

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                simpleDateFormat.format(calendar.getTime());

                myMonth+= 1;

                //Show selected date and time
                showDateTime.setText(simpleDateFormat.format(calendar.getTime()));
                showDateTime.setVisibility(View.VISIBLE);

                //Toast.makeText(CreateActivity.this, "Data: "+simpleDateFormat.format(calendar.getTime()), Toast.LENGTH_LONG).show();

            };

            new TimePickerDialog(CreateActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        };

        new DatePickerDialog(CreateActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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

                return true;

            }
            else
            {
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

        //Date button
        if(v.getId() == R.id.create_trip_date_picker){
            showDateDialog();
        }
        else {

            //Create button
            if(v.getId() == R.id.advance_create_trip){
                goToActivity();
            }
        }

    }


    /**
     * Callback function to be invoked when an item in this view has been selected
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            //Spinner from
            if(parent.getId() == R.id.createCitiesSpinnerFrom){

                if(position != 0)
                {
                    fromCreate= parent.getItemAtPosition(position).toString();
                    fromCreatePos = 1;
                }
                else
                {
                    fromCreatePos = 0;
                }


            }


            else {
                //Spinner to
                if (parent.getId() == R.id.createCitiesSpinnerTo) {

                    if(position != 0){
                        toCreate = parent.getItemAtPosition(position).toString();
                    }

                    if(position != 0) {
                        toCreate = parent.getItemAtPosition(position).toString();
                        toCreatePos = 1;
                    }
                    else
                    {
                        toCreatePos = 0;
                    }
                }
            }


    }


    /**
     * Callback function to be invoked when the selection disappears from this view
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}