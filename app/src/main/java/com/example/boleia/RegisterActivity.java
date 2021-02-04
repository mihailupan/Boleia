package com.example.boleia;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userID;

    private EditText emailEdit, passwordEdit, nameEdit, phoneEdit;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        TextView title = (TextView) findViewById(R.id.register_title_text_view);

        progressBar = (ProgressBar) findViewById(R.id.register_progress_bar);

        emailEdit = (EditText) findViewById(R.id.register_email_edit_text);

        passwordEdit = (EditText) findViewById(R.id.register_password_edit_text);

        nameEdit = (EditText) findViewById(R.id.register_name_edit_text);

        phoneEdit = (EditText) findViewById(R.id.register_phone_edit_text);

        Button confirmRegisterButton = (Button) findViewById(R.id.register_register_button);
        confirmRegisterButton.setOnClickListener(this);
    }

    /**
     * Function to see which view was clicked and do something depending on the view clicked
     * @param v View selected
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_register_button) {
            registerUser();
        }
    }

    /**
     * Regist new user on the application
     */
    private void registerUser(){
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String name = nameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();

        if (checkField(email.isEmpty(), emailEdit, getString(R.string.email_required))) return;
        if (checkField(!Patterns.EMAIL_ADDRESS.matcher(email).matches(), emailEdit, getString(R.string.invalid_email))) return;
        if (checkField(password.isEmpty(), passwordEdit, getString(R.string.password_required))) return;
        if (checkField(password.length() < 6, passwordEdit, getString(R.string.password_min_caract))) return;
        if (checkField(name.isEmpty(), nameEdit, getString(R.string.name_required))) return;
        if (checkField(phone.isEmpty(), phoneEdit, getString(R.string.phone_required))) return;

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        userID = mAuth.getCurrentUser().getUid();

                        //Access document that belongs to user
                        DocumentReference documentReference = mStore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("phone", phone);
                        documentReference.set(user).addOnSuccessListener(aVoid -> Toast.makeText(RegisterActivity.this, R.string.user_registered, Toast.LENGTH_LONG).show());
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, R.string.register_failed, Toast.LENGTH_LONG).show();
                    }

                    progressBar.setVisibility(View.GONE);
                });
    }

    private boolean checkField(boolean empty, EditText emailEdit, String s) {
        if (empty) {
            emailEdit.setError(s);
            emailEdit.requestFocus();
            return true;
        }
        return false;
    }
}