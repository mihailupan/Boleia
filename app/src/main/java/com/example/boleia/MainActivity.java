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
        Button loginButton = (Button) findViewById(R.id.loginMainButton);
        loginButton.setOnClickListener(this);

        //EditText
        emailEditText = (EditText) findViewById(R.id.emailMainEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordMainEditText);

        //TextView
        TextView registerTextView = (TextView) findViewById(R.id.registerMainTextView);
        registerTextView.setOnClickListener(this);

        //ProgressBar
        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);

    }


    /**
     * Function to see which view was clicked and do something depending on the view clicked
     * @param v View selected
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerMainTextView) {

            startActivity(new Intent(this, RegisterActivity.class));

        }if (v.getId() == R.id.loginMainButton){
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
                Toast.makeText(MainActivity.this, "Login falhou! Verifique os seus dados!", Toast.LENGTH_LONG).show();
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
            emailEditText.setError("Introduza o e-mail!");
            emailEditText.requestFocus();
            return true;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("E-mail é invalido!");
            emailEditText.requestFocus();
            return true;
        }
        if (password.isEmpty()){
            passwordEditText.setError("Introduza a password!");
            passwordEditText.requestFocus();
            return true;
        }
        if (password.length()<6){
            passwordEditText.setError("A password tem no mínimo 6 caracteres!");
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