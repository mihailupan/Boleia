package com.example.boleia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private TextView title;
    private EditText emailEdit, passwordEdit, nameEdit, phoneEdit;
    private Button confirmRegisterButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        title = (TextView) findViewById(R.id.registerTitleTextView);

        progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);

        emailEdit = (EditText) findViewById(R.id.editEmailRegister);

        passwordEdit = (EditText) findViewById(R.id.editPasswordRegister);

        nameEdit = (EditText) findViewById(R.id.editNameRegister);

        phoneEdit = (EditText) findViewById(R.id.editPhoneRegister);

        confirmRegisterButton = (Button) findViewById(R.id.confirmRegisterButton);
        confirmRegisterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirmRegisterButton:
                //startActivity(new Intent(this, MainActivity.class));
                registerUser();
                break;
        }
    }

    private void registerUser(){
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String name = nameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();

        if (email.isEmpty()){
            emailEdit.setError("É necessário o e-mail!");
            emailEdit.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdit.setError("E-mail não é válido!");
            emailEdit.requestFocus();
            return;
        }
        if (password.isEmpty()){
            passwordEdit.setError("É necessária a password!");
            passwordEdit.requestFocus();
            return;
        }
        if (password.length() <6 ){
            passwordEdit.setError("Mínimo de 6 caracteres!");
            passwordEdit.requestFocus();
            return;
        }
        if(name.isEmpty()){
            nameEdit.setError("É necessário o nome!");
            nameEdit.requestFocus();
            return;
        }
        if (phone.isEmpty()){
            phoneEdit.setError("É necessário o telemóvel!");
            phoneEdit.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(email,name,phone);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Utilizador foi registado!", Toast.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(RegisterActivity.this, "Registo falhou! Tente novamente!", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }else {
                            Toast.makeText(RegisterActivity.this, "Registo falhou! Tente novamente!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}