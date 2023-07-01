package com.example.slagalica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.slagalica.dataBase.DBHelper;

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
                boolean checkUsernameExists = dbHelper.checkUsernameExists(username);
                if (checkUsernameExists) {
                    Toast.makeText(getApplicationContext(), "Username vec postoji.", Toast.LENGTH_SHORT).show();

                } else {
                    if (!username.equals("") && !email.equals("") && !password.equals("") && password.equals(password2)) {
                        dbHelper.insertUser(email, username, password);
                        Toast.makeText(getApplicationContext(), "Uspjesno registrovanje.", Toast.LENGTH_SHORT).show();
                        Intent homeIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Problem kod registracije(Prazna polja ili sifra se ne podudara).", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}