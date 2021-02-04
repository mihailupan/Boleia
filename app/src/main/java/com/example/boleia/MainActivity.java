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
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    private EditText emailEditText, passwordEditText;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        verifyUserLogin();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reference
        mAuth = FirebaseAuth.getInstance();

        //Button
        Button loginButton = (Button) findViewById(R.id.main_login_button);
        loginButton.setOnClickListener(this);

        //EditText
        emailEditText = (EditText) findViewById(R.id.main_email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.main_password_edit_text);

        //TextView
        TextView registerTextView = (TextView) findViewById(R.id.main_register_text_view);
        registerTextView.setOnClickListener(this);

        //ProgressBar
        progressBar = (ProgressBar) findViewById(R.id.main_progress_bar);

    }


    /**
     * Function to see which view was clicked and do something depending on the view clicked
     * @param v View selected
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_register_text_view) {

            startActivity(new Intent(this, RegisterActivity.class));

        }if (v.getId() == R.id.main_login_button){
            userLogin();
        }
    }

    /**
     * Log user on the application
     */
    private void userLogin(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (checkField(email, password)) return;

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                finish();
            }else{
                Toast.makeText(MainActivity.this, R.string.login_failed, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Check every edit text
     * @param email Email user introduced
     * @param password Password user introduced
     * @return Boolean value, true if any rules are not made
     */
    private boolean checkField(String email, String password) {
        if(email.isEmpty()){
            emailEditText.setError(getString(R.string.email_required));
            emailEditText.requestFocus();
            return true;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError(getString(R.string.invalid_email));
            emailEditText.requestFocus();
            return true;
        }
        if (password.isEmpty()){
            passwordEditText.setError(getString(R.string.password_required));
            passwordEditText.requestFocus();
            return true;
        }
        if (password.length()<6){
            passwordEditText.setError(getString(R.string.password_min_caract));
            passwordEditText.requestFocus();
            return true;
        }
        return false;
    }

    /**
     * Function to verify is user logged
     */
    private void verifyUserLogin(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        }
    }
}