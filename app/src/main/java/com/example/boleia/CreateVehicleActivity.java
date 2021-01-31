package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateVehicleActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    String [] data;
    Button createTravelButton;
    ImageButton openGalleryButtonVehicle, captureImageButtonVehicle;
    EditText editBrandVehicle, editModelVehicle, editLicensePlateVehicle, editSeatNumberVehicle;
    ImageView vehiclePhoto;
    Uri contentUri;
    String currentPhotoPath;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userID;
    private String userName;
    private String userEmail;
    private String userPhone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vehicle);

        //EditTexts
        editBrandVehicle = findViewById(R.id.editBrandVehicle);
        editModelVehicle = findViewById(R.id.editModelVehicle);
        editLicensePlateVehicle = findViewById(R.id.editLicensePlateVehicle);
        //editSeatNumberVehicle = findViewById(R.id.editSeatNumberVehicle);

        //ImageView
        vehiclePhoto = findViewById(R.id.carPhoto);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        createTravelButton = findViewById(R.id.createTravelButton);
        createTravelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTravel();
                //startActivity(new Intent(CreateVehicleActivity.this, TravelsActivity.class));
            }
        });


        //Button to capture
        captureImageButtonVehicle = findViewById(R.id.captureImageButtonVehicle);
        captureImageButtonVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        //Gallery button
        openGalleryButtonVehicle = findViewById(R.id.openGalleryButtonVehicle);
        openGalleryButtonVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        Bundle bundle = getIntent().getExtras();
        data = bundle.getStringArray("data");

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

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
        if ( checkField(brandVehicle.isEmpty(), editBrandVehicle, "É necessário o modelo do carro")) return;
        if ( checkField(modelVehicle.isEmpty(), editModelVehicle, "É necessário a marca do carro")) return;
        if ( checkField(licensePlateVehicle.isEmpty(), editLicensePlateVehicle, "É necessário a matrícula do carro")) return;


        //Check if photo was chosen
        if(vehiclePhoto.getVisibility() == View.INVISIBLE){
            Toast.makeText(CreateVehicleActivity.this, "É necessário tirar ou escolher uma fotografia!", Toast.LENGTH_LONG).show();
            return;
        }

        //Save data
        //Toast.makeText(CreateVehicleActivity.this, "Guarda os dados", Toast.LENGTH_LONG).show();



        String txtToDB = getData();
        Toast.makeText(CreateVehicleActivity.this, txtToDB, Toast.LENGTH_LONG).show();

        getUserData();
        //Send data to cloud firestore and storage
        sendDataToFirebaseCloudFirestore();


    }

    private void getUserData(){

        DocumentReference docRef = mStore.collection("users").document(userID);

        final String[] userN = new String[1];
        final String[] userE = new String[1];
        final String[] userP = new String[1];

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    userN[0] = documentSnapshot.getString("name");
                    userE[0] = documentSnapshot.getString("email");
                    userP[0] = documentSnapshot.getString("phone");
                }
                else{
                    Log.d("Document", "No data "+userID);
                }

            }
        });
/*        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()) {
                        Log.d("Document", doc.getData().toString());


                        userN[0] = doc.getString("name");
                        userE[0] = doc.getString("email");
                        userP[0] = doc.getString("phone");

                    }
                    else{
                        Log.d("Document", "No data "+userID);
                    }
                }
            }
        });*/

        this.userName = userN[0];
        this.userEmail = userE[0];
        this.userPhone = userP[0];
    }


    private void sendDataToFirebaseCloudFirestore() {

        Map<String, Object> user = new HashMap<>();
        user.put("userID", userID);

        user.put("name", this.userName);
        user.put("email", this.userEmail);
        user.put("phone", this.userPhone);

        //Toast.makeText(CreateVehicleActivity.this, info[0]+""+info[1]+""+info[2], Toast.LENGTH_LONG).show();

        user.put("from", data[0]);
        user.put("to", data[1]);

        String formatedDate =  data[2]+"-"+data[3]+"-"+data[4];
        user.put("date", formatedDate);

        String formatedTime =  data[5]+":"+data[6];
        user.put("time", formatedTime);

        user.put("meetingPointLat", data[7]);
        user.put("meetingPointLng", data[8]);

        user.put("vehicleBrand", editBrandVehicle.getText().toString());
        user.put("vehicleModel", editModelVehicle.getText().toString());
        user.put("vehicleLicensePlate", editLicensePlateVehicle.getText().toString());


        String vehiclePhotoName = userID+""+data[0]+""+data[1]+""+data[2]+""+data[3]+""+data[4]+""+data[5]+""+data[6];

        user.put("vehiclePhotoName", vehiclePhotoName);

        //Access document that belongs to user
        DocumentReference documentReference = mStore.collection("travels").document(vehiclePhotoName);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateVehicleActivity.this, "Dados da viagem submetidos!", Toast.LENGTH_LONG).show();
            }
        });

        updloadImageToFirebase(vehiclePhotoName);
    }

    /**
     * Upload image to firebase storage
     * @param name
     */
    private void updloadImageToFirebase(String name) {

        StorageReference image  =  storageReference.child(userID+"/"+name); //images/image.jpg
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CreateVehicleActivity.this, "Upload Done!", Toast.LENGTH_SHORT).show();

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateVehicleActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * @param var Variable to check if it's empty
     * @param editTxt Variable to set error
     * @param s Message to show when error if var is empty
     * @return
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
     * @param requestCode
     * @param permissions
     * @param grantResults
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
                Toast.makeText(this, "É necessária ativar a permissão para utilizar a camara!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Result of apening camera to take photo or opening gallery to choose photo
     * @param requestCode
     * @param resultCode
     * @param data
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

        if (requestCode == GALLERY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK)
            {
                contentUri = data.getData();
                Picasso.get().load(contentUri).into(vehiclePhoto);
            }
        }

    }


    /**
     * Auxiliary function to get the file extension
     * @param contentUri
     * @return
     */
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
     * @return Organized string to put on database
     */
    private String getData() {

        String str = "";
        for (int i = 0; i < data.length; i++)
        {
            str += data[i] +"\n";
        }
        return str;
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