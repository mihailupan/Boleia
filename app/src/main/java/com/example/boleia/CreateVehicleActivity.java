 package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateVehicleActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    String [] data;
    Button createTravelButton;
    ImageButton openGalleryButtonVehicle, captureImageButtonVehicle;
    EditText editBrandVehicle, editModelVehicle, editLicensePlateVehicle;
    ImageView vehiclePhoto;
    Uri contentUri;
    String currentPhotoPath;
    private StorageReference storageReference;
    private FirebaseFirestore mStore;
    private String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vehicle);

        //EditTexts
        editBrandVehicle = findViewById(R.id.create_register_vehicle_brand_edit_text);
        editModelVehicle = findViewById(R.id.create_register_vehicle_vehicle_model_edit_text);
        editLicensePlateVehicle = findViewById(R.id.create_register_vehicle_vehicle_license_plate_edit_text);

        //ImageView
        vehiclePhoto = findViewById(R.id.create_register_vehicle_car_photo_image_view);

        //BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        //Button to create travel
        createTravelButton = findViewById(R.id.create_register_vehicle_create_travel_button);
        createTravelButton.setOnClickListener(this);


        //Button to capture
        captureImageButtonVehicle = findViewById(R.id.create_register_vehicle_camera_image_button);
        captureImageButtonVehicle.setOnClickListener(this);

        //Gallery button
        openGalleryButtonVehicle = findViewById(R.id.create_register_vehicle_gallery_image_button);
        openGalleryButtonVehicle.setOnClickListener(this);


        getBundleContent();

        //References
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    /**
     * Function to get the content passed to the activity
     */
    private void getBundleContent() {
        Bundle bundle = getIntent().getExtras();
        data = bundle.getStringArray("data");
    }


    /**
     * Open gallery to choose photo
     */
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

    /**
     * Function to create data for new trip and save data on database
     */
    private void createTravel() {
        String brandVehicle = editBrandVehicle.getText().toString().trim();
        String modelVehicle = editModelVehicle.getText().toString().trim();
        String licensePlateVehicle = editLicensePlateVehicle.getText().toString().trim();


        //Check if every field isn't empty
        if ( checkField(brandVehicle.isEmpty(), editBrandVehicle, getString(R.string.car_brand_required))) return;
        if ( checkField(modelVehicle.isEmpty(), editModelVehicle, getString(R.string.car_model_required))) return;
        if ( checkField(licensePlateVehicle.isEmpty(), editLicensePlateVehicle, getString(R.string.car_license_plate_required))) return;


        //Check if photo was chosen
        if(vehiclePhoto.getVisibility() == View.INVISIBLE){
            Toast.makeText(CreateVehicleActivity.this, R.string.car_photo_required, Toast.LENGTH_LONG).show();
            return;
        }

        getUserData();

        Intent intent = new Intent(CreateVehicleActivity.this, TravelsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    /**
     * Function to get the current user information
     */
    private void getUserData(){

        DocumentReference docRef = mStore.collection("users").document(userID);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phone = documentSnapshot.getString("phone");
                try {
                    sendDataToFirebaseCloudFirestore(name,email,phone);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.d("Document", "No data "+userID);
            }

        });


    }


    /**
     * Function to send information to firestore cloud database
     * @param name1 Current user name
     * @param email1 Current user email
     * @param phone1 Current usr phone number
     * @throws ParseException Execption raised if dateToTimestamp() throws an error
     */
    private void sendDataToFirebaseCloudFirestore(String name1, String email1, String phone1) throws ParseException {

        Map<String, Object> user = new HashMap<>();
        user.put("userID", userID);

        user.put("name", name1);
        user.put("email", email1);
        user.put("phone", phone1);


        user.put("from", data[0]); //FromCity is on 1st pos
        user.put("to", data[1]); //ToCity is on 2nd pos


        String formatedDate =  data[2]+"-"+data[3]+"-"+data[4]; //Day, Month, Year are on 3rd, 4th and 5th, in this order
        user.put("date", formatedDate);

        String formatedTime =  getFormattedTime(data[5], data[6]); //Hour, Minute are on 6th and 7th, in this order
        user.put("time", formatedTime);

        user.put("meetingPointLat", data[7]); //MeetingPoint Latitude is on 8th pos
        user.put("meetingPointLng", data[8]); //MeetingPoint Latitude is on 9th pos

        user.put("vehicleBrand", editBrandVehicle.getText().toString());
        user.put("vehicleModel", editModelVehicle.getText().toString());
        user.put("vehicleLicensePlate", editLicensePlateVehicle.getText().toString());


        //VehiclePhotoName will be a string with (userId, fromCity, toCity, day, month, year, hour, minute), info related to the travel created
        String vehiclePhotoName = userID+""+data[0]+""+data[1]+""+data[2]+""+data[3]+""+data[4]+""+data[5]+""+data[6];

        user.put("vehiclePhotoName", vehiclePhotoName);

        user.put("timestamp", dateToTimestamp(formatedDate));

        //Access document that belongs to user
        DocumentReference documentReference = mStore.collection("travels").document(vehiclePhotoName);
        documentReference.set(user)
                .addOnSuccessListener(successSubmit -> Toast.makeText(CreateVehicleActivity.this, R.string.travel_data_submited, Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(CreateVehicleActivity.this, R.string.submited_travel_error, Toast.LENGTH_LONG).show());

        updloadImageToFirebase(vehiclePhotoName);

    }

    /**
     * Convert date to timestamp
     * @param date Date to be converted
     * @return Converted date
     * @throws ParseException Parse exception
     */
    private long dateToTimestamp(String date) throws ParseException {
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date dateF = (Date)formatter.parse(date);
        return dateF.getTime();
    }

    /**
     * @param hour String hour
     * @param minute String minute
     * @return Formatted data to be stored in Firestore
     */
    private String getFormattedTime(String hour, String minute) {

        if(hour.length() == 1){
            hour = "0"+hour;
        }

        if(minute.length() == 1){
            minute = "0"+minute;
        }

        return hour+":"+minute;
    }

    /**
     * Upload image to firebase storage
     * @param name String that has the file name to be used
     */
    private void updloadImageToFirebase(String name) {

        StorageReference image  =  storageReference.child(userID+"/"+name); //images/image.jpg

        try {
            image.putFile(contentUri)
                    .addOnSuccessListener(taskSnapshot -> Toast.makeText(CreateVehicleActivity.this, R.string.photo_sended, Toast.LENGTH_SHORT).show()).
                    addOnFailureListener(e -> Toast.makeText(CreateVehicleActivity.this, R.string.photo_sended_error, Toast.LENGTH_SHORT).show());
        }
        catch (SecurityException e){
            Toast.makeText(this, "Erro de segurança ao enviar fotografia!", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * @param var Variable to check if it's empty
     * @param editTxt Variable to set error
     * @param s Message to show when error if var is empty
     * @return Boolean value, true if user has to check field
     */
    private boolean checkField(boolean var, EditText editTxt, String s) {
        if (var) {
            editTxt.setError(s);
            editTxt.requestFocus();
            return true;
        }
        return false;
    }

    /**
     * Take photo if app has permission, if not, ask user for permission
     */
    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }
        else
        {
            dispatchTakePictureIntent();
        }
    }


    /**
     * Result of permission asked, if request code equals the CAMERA_PERM_CODE, user can take photo
     * @param requestCode Request code
     * @param permissions Array of permission
     * @param grantResults Array of grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Has permission to access camera
        if(requestCode == CAMERA_PERM_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
               dispatchTakePictureIntent();
            }
            else
            {
                Toast.makeText(this, R.string.activate_camera_required, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Result of opening camera to take photo or opening gallery to choose photo
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
           if(resultCode == Activity.RESULT_OK)
           {
               File f = new File(currentPhotoPath);
               Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
               contentUri = Uri.fromFile(f);
               mediaScanIntent.setData(contentUri);
               this.sendBroadcast(mediaScanIntent);
               Picasso.get().load(contentUri).into(vehiclePhoto);
           }
        }

        else
        {
            if (requestCode == GALLERY_REQUEST_CODE) {
                if(resultCode == Activity.RESULT_OK)
                {
                    contentUri = data.getData();
                    Picasso.get().load(contentUri).into(vehiclePhoto);
                    vehiclePhoto.setVisibility(View.VISIBLE);
                }
            }
        }


    }


    /**
     * Creates an image with a filename
     * @return Image file
     * @throws IOException Input ot output exception
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //Do not save on gallery
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //Save on gallery
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    /**
     * Function to ask MEDIASTORE to take photo
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                  //TODO Write error message
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.boleia.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
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

        //Create travel button
        if (v.getId() == R.id.create_register_vehicle_create_travel_button) {
            this.createTravel();
        }
        else
        {
            if(v.getId() == R.id.create_register_vehicle_camera_image_button){
                this.askCameraPermissions();
            }
            else
            {
                if(v.getId() == R.id.create_register_vehicle_gallery_image_button)
                {
                    this.openGallery();
                }
            }
        }
    }


}