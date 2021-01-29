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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userID;

    private TextView title;
    private EditText emailEdit, passwordEdit, nameEdit, phoneEdit;
    private Button confirmRegisterButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

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

        if (checkField(email.isEmpty(), emailEdit, "É necessário o e-mail!")) return;
        if (checkField(!Patterns.EMAIL_ADDRESS.matcher(email).matches(), emailEdit, "E-mail não é válido!")) return;
        if (checkField(password.isEmpty(), passwordEdit, "É necessária a password!")) return;
        if (checkField(password.length() < 6, passwordEdit, "Mínimo de 6 caracteres!")) return;
        if (checkField(name.isEmpty(), nameEdit, "É necessário o nome!")) return;
        if (checkField(phone.isEmpty(), phoneEdit, "É necessário o telemóvel!")) return;

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Toast.makeText(RegisterActivity.this, "Utilizador foi registado!", Toast.LENGTH_LONG).show();
                            userID = mAuth.getCurrentUser().getUid();

                            //Access document that belongs to user
                            DocumentReference documentReference = mStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("phone", phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Utilizador foi registado!", Toast.LENGTH_LONG).show();
                                }
                            });
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Registo falhou! Tente novamente!", Toast.LENGTH_LONG).show();
                        }

                        progressBar.setVisibility(View.GONE);
                            //User user = new User(email,name,phone);

               /*         FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Utilizador foi registado!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                    }else {
                                        Toast.makeText(RegisterActivity.this, "Registo falhou! Tente novamente!", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });*/
                        /*else {
                            Toast.makeText(RegisterActivity.this, "Registo falhou! Tente novamente!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }*/
                    }
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