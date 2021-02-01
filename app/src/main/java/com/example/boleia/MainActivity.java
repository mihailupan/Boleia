package com.example.boleia;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    private Button loginButton;
    private TextView registerTextView;
    private EditText emailEditText, passwordEditText;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        verifyUserLogin();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        loginButton = (Button) findViewById(R.id.loginMainButton);
        loginButton.setOnClickListener(this);

        emailEditText = (EditText) findViewById(R.id.emailMainEditText);

        passwordEditText = (EditText) findViewById(R.id.passwordMainEditText);

        registerTextView = (TextView) findViewById(R.id.registerMainTextView);
        registerTextView.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);



    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerMainTextView) {

            startActivity(new Intent(this, RegisterActivity.class));
            finish();

        }if (v.getId() == R.id.loginMainButton){
            userLogin();
        }
    }

    private void userLogin(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(email.isEmpty()){
            emailEditText.setError("Introduza o e-mail!");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("E-mail é invalido!");
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()){
            passwordEditText.setError("Introduza a password!");
            passwordEditText.requestFocus();
            return;
        }
        if (password.length()<6){
            passwordEditText.setError("A password tem no mínimo 6 caracteres!");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "Login falhou! Verifique os seus dados!", Toast.LENGTH_LONG).show();
                    //progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void verifyUserLogin(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        }
    }
}