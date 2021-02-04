package com.example.boleia;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TravelsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference travelRef = db.collection("travels");

    private TravelAdapter adapter;
    private FloatingActionButton fab_save_travels;
    private List<Travel> travelList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travels);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        fab_save_travels = findViewById(R.id.fab_create_travels_pdf);
        fab_save_travels.setOnClickListener(this);

        getTravelsFromFirestore();
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

    /**
     * Function to see which view was clicked and do something depending on the view clicked
     * @param v View selected
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab_create_travels_pdf){
            createPDF();
        }
    }

    /**
     * Function to create PDF with user travels information
     */
    private void createPDF() {
        String title = "MinhasViagens";
        String path = getExternalFilesDir(null).toString() + "/"+title+".pdf";
        File file = new File(path);

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        document.open();
        
        try {
            document.add(new Paragraph(title));
            document.add(new Paragraph("\n"));

        } catch (DocumentException e) {
            //TODO
            Toast.makeText(this, "Error while creating PDF!", Toast.LENGTH_SHORT).show();
        }


        for (int i = 0; i < travelList.size(); i++) {
            try {
                document.add(new Paragraph("Nome: "+travelList.get(i).getName()+"\\n"));
                document.add(new Paragraph("De: "+travelList.get(i).getFrom()+"\\n"));
                document.add(new Paragraph("Para: "+travelList.get(i).getTo()+"\\n"));
                document.add(new Paragraph("Data: "+travelList.get(i).getDate()+"\\n"));
                document.add(new Paragraph("Hora: "+travelList.get(i).getTime()+"\\n"));
                document.add(new Paragraph("Marca Veículo: "+travelList.get(i).getVehicleBrand()+"\\n"));
                document.add(new Paragraph("Modelo Veículo: "+travelList.get(i).getVehicleModel()+"\\n"));
                document.add(new Paragraph("Matrícula Veículo: "+travelList.get(i).getVehicleLicensePlate()+"\\n"));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(title));
            }
            catch (DocumentException e){
                //TODO
                Toast.makeText(this, "Error while creating PDF!", Toast.LENGTH_LONG).show();
            }

        }

        //TODO
        Toast.makeText(this, "PDF File created successfully!", Toast.LENGTH_LONG).show();

        document.close();

    }

    /**
     * Get all the current ueser travels from firestore
     */
    private void getTravelsFromFirestore(){

        travelList = new ArrayList<>();

        travelRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereGreaterThanOrEqualTo("timestamp", System.currentTimeMillis())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Travel travel = new Travel(document.getString("userID"),document.getString("name"),document.getString("email"),
                                    document.getString("phone"),document.getString("from"),document.getString("to")
                                    ,document.getString("date"),document.getString("time"),document.getString("meetingPointLat"),
                                    document.getString("meetingPointLng"),document.getString("vehicleBrand"),document.getString("vehicleModel"),
                                    document.getString("vehicleLicensePlate"),document.getString("vehiclePhotoName"));

                            travelList.add(travel);
                        }
                    } else {
                        //TODO
                        Toast.makeText(TravelsActivity.this, "Erro ao obter informação das viagens!", Toast.LENGTH_SHORT).show();
                    }
                });


    }

}