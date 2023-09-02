package com.example.slagalica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.slagalica.dataBase.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword1;
    private EditText etPassword2;
    private Button btnReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new DBHelper(this);

        btnReg = findViewById(R.id.registerButton);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail = findViewById(R.id.RegistrationEmailEditText);
                etUsername = findViewById(R.id.RegistrationUsernameEditText);
                etPassword1 = findViewById(R.id.RegistrationPasswordEditText);
                etPassword2 = findViewById(R.id.RegistrationPasswordEditText2);

                String email = etEmail.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword1.getText().toString();
                String password2 = etPassword2.getText().toString();
                    if (!username.equals("") && !email.equals("") && !password.equals("") && password.equals(password2)) {
                        register(email,password,username);

                    } else {
                        Toast.makeText(getApplicationContext(), "Problem kod registracije(Prazna polja ili sifra se ne podudara).", Toast.LENGTH_SHORT).show();
                    }

            }
        });

    }
    public void register(String email,String password,String username){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                            usersRef.child(user.getUid()).child("email").setValue(email);
                            usersRef.child(user.getUid()).child("password").setValue(password);
                            usersRef.child(user.getUid()).child("username").setValue(username);
                            usersRef.child(user.getUid()).child("brojPobjeda").setValue(0);
                            Toast.makeText(getApplicationContext(), "Uspješno registrovanje.", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(homeIntent);
                            finish();
                        }
                    } else {
                            Toast.makeText(getApplicationContext(), "Registracija nije uspela. Proverite unos i pokušajte ponovo ili E-mail vec postoji.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}