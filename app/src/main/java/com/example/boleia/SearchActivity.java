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

public class SearchActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    Spinner fromSpinner;
    Spinner toSpinner;
    String selectedFromCity;
    String selectedToCity;
    Button dateButton;
    int myDay, myMonth, myYear, myHour, myMinute;
    TextView dateTimeTextView;
    String defaultSpinnerValue;
    int selectedFromCityPos, selectedToCityPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        defaultSpinnerValue = String.valueOf(R.string.defaul_spinner_value);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //Pos of item on spinner
        selectedFromCityPos = 0;
        selectedToCityPos = 0;

        //Initializes the spinners
        fromSpinner = findViewById(R.id.search_from_spinner);
        toSpinner = findViewById(R.id.search_to_spinner);

        //Saves the values from spinners
        selectFromSpinners();

        //Button to choose date and time
        dateButton = findViewById(R.id.search_date_picker_button);
        dateButton.setOnClickListener(this);

        //Next Button
        Button nextButton = findViewById(R.id.search_next_button);
        nextButton.setOnClickListener(this);

        //Shows the date and time in a textView
        dateTimeTextView = findViewById(R.id.search_date_time_text_view);

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

                myMonth += 1;

            };


            String date = myDay+"-"+myMonth+"-"+myYear;
            dateTimeTextView.setText(date);
            dateTimeTextView.setVisibility(View.VISIBLE);

            new TimePickerDialog(SearchActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        };

        new DatePickerDialog(SearchActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    /**
     * Function to create spinner adapters and set the adapter functions
     */
    private void selectFromSpinners(){

        ArrayAdapter<CharSequence> fromAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);
        fromSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> toAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);
        toSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Function to pass data to another activity
     */
    private void sendData() {

        if (myYear == 0 && myMonth == 0 && myDay == 0 && myHour == 0 && myMinute == 0) {
            Toast.makeText(SearchActivity.this, "É necessário escolher uma data para a viagem!", Toast.LENGTH_LONG).show();
            return;
        }

        if((selectedFromCityPos == 0 || selectedToCityPos == 0) || (selectedToCity.equals(selectedFromCity)))
        {
            Toast.makeText(this, "Por favor, selecione uma cidade válida!", Toast.LENGTH_SHORT).show();
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
        double [] coordinates = getCityCoordinates(selectedFromCity);
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
     * Function to get the item selected on the navigation item
     * @param item Item selected in the menuItem
     * @return Boolean value, will return true if any navigation item is clicked
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.searchNav) {
            return true;

        }
        else
        {
            if (item.getItemId() == R.id.createNav) {

                startActivity(new Intent(this, CreateActivity.class));
                overridePendingTransition(0,0);
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
        if (v.getId()== R.id.search_date_picker_button){
            showDateDialog();
        }
        if (v.getId()== R.id.search_next_button){
            sendData();
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
        if(parent.getId() == R.id.search_from_spinner){


                if(position != 0)
                {
                    selectedFromCity= parent.getItemAtPosition(position).toString();
                    selectedFromCityPos = 1;
                }
                else
                {
                    selectedFromCityPos = 0;
                }


        }


        else {
            //Spinner to
            if (parent.getId() == R.id.search_to_spinner) {


                if(position != 0) {
                    selectedToCity = parent.getItemAtPosition(position).toString();
                    selectedToCityPos = 1;
                }
                else
                {
                    selectedToCityPos = 0;
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