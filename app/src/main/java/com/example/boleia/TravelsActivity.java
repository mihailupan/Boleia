package com.example.boleia;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class TravelsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference travelRef = db.collection("travels");

    private TravelAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travels);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        setUpRecyclerView();
    }


    /**
     * Function to setUp the recycler view
     */
    private void setUpRecyclerView() {

        Query query = travelRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereGreaterThanOrEqualTo("timestamp", System.currentTimeMillis());


        FirestoreRecyclerOptions<Travel> options = new FirestoreRecyclerOptions.Builder<Travel>()
                .setQuery(query, Travel.class).build();

        adapter = new TravelAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.travels_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(recyclerView);
        adapter.notifyDataSetChanged();
    }

    /**
     * Function to make adapter start listening any changes when activity is being used
     */
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    /**
     * Function to make adapter stop listening any changes when activity is not being used
     */
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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

                startActivity(new Intent(this, CreateActivity.class));
                overridePendingTransition(0,0);
                return true;

            }
            else
            {
                if (item.getItemId() == R.id.travelsNav) {

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




}