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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    private String userID;
    private String currentPhotoPath;
    private ImageView profilePhoto;
    private StorageReference storageReference;



    //Codes
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //ImageView
        profilePhoto = findViewById(R.id.profile_photo_image_view);

        //References
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = mAuth.getCurrentUser().getUid();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.searchNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Button logoutButton = (Button) findViewById(R.id.profile_logout_button);
        logoutButton.setOnClickListener(this);





        DocumentReference docRef = mStore.collection("users").document(userID);
        setProfileInformation(docRef);

        setProfilePhoto();


        //Button to capture
        ImageButton captureImageButtonProfile = findViewById(R.id.profile_camera_image_button);
        captureImageButtonProfile.setOnClickListener(this);

        //Gallery button
        ImageButton openGalleryButtonProfile = findViewById(R.id.profile_gallery_image_button);
        openGalleryButtonProfile.setOnClickListener(this);

    }

    /**
     * Function to set user information on textViews
     * @param docRef Document refence to get user information
     */
    private void setProfileInformation(DocumentReference docRef) {

        final TextView nameTextView = (TextView) findViewById(R.id.profile_name_text_view);
        final TextView emailTextView = (TextView) findViewById(R.id.profile_email_text_view);
        final TextView phoneTextView = (TextView) findViewById(R.id.profile_phone_text_view);

        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()) {
                    Log.d("Document", doc.getData().toString());


                    String name = doc.getString("name");
                    String email = doc.getString("email");
                    String phone = doc.getString("phone");


                    nameTextView.setText(name);
                    emailTextView.setText(email);
                    phoneTextView.setText(phone);

                    nameTextView.setVisibility(View.VISIBLE);
                    emailTextView.setVisibility(View.VISIBLE);
                    phoneTextView.setVisibility(View.VISIBLE);
                }
                else{
                    Log.d("Document", "No data "+userID);
                }
            }
        });
    }

    /**
     * Function to load profile photo to imageView
     */
    private void setProfilePhoto() {
        storageReference.child(userID+"/"+ "profile").getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    // Got the download URL for 'userID/profile.png', set profile picture
                    Picasso.get().load(uri).into(profilePhoto);
                    profilePhoto.setVisibility(View.VISIBLE);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Toast.makeText(ProfileActivity.this, R.string.photo_download_error, Toast.LENGTH_SHORT).show();
        });
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
                    return item.getItemId() == R.id.profileNav;
                }
            }

        }
    }

    /**
     * Function to see which view was clicked and do something depending on the view clicked
     * @param v View selected
     */
    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.profile_logout_button){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else
        {
            if(v.getId() == R.id.profile_camera_image_button)
            {
                this.askCameraPermissions();
            }
            else
            {
                if(v.getId() == R.id.profile_gallery_image_button)
                {
                    this.openGallery();
                }
            }
        }
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
     * Result of opening camera to take photo or opening gallery to choose photo
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri contentUri;
        if (requestCode == CAMERA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK)
            {
                File f = new File(currentPhotoPath);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                updlodImageToFirebase(contentUri);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK)
            {
                contentUri = data.getData();
                updlodImageToFirebase(contentUri);
            }
        }

    }


    /**
     * Upload image to firebase storage
     * @param contentUri URI that identifies data in a provider
     */
    private void updlodImageToFirebase(Uri contentUri) {

        StorageReference image  =  storageReference.child(userID+"/"+ "profile");
        image.putFile(contentUri)
                .addOnSuccessListener(taskSnapshot -> image.getDownloadUrl()
                .addOnSuccessListener(uri -> Picasso.get().load(uri).into(profilePhoto)))
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, R.string.profile_photo_load_error, Toast.LENGTH_SHORT).show());
    }


    /**
     * Open gallery to choose photo
     */
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

}