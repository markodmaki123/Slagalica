package com.example.slagalica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.slagalica.dataBase.DBHelper;

public class LoginActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHelper = new DBHelper(this);

        String emailTest = "marko@gmail.com";
        String usernameTest = "Marko";
        String passwordTest = "marko1233";
        dbHelper.insertUser(emailTest, usernameTest, passwordTest);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameET = findViewById(R.id.username_edit_text);
                EditText passwordET = findViewById(R.id.password_edit_text);

                String username = usernameET.getText().toString();
                String password = passwordET.getText().toString();

                boolean loginSuccessful = dbHelper.loginUser(username, password);
                if (loginSuccessful) {
                    Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Prijavljivanje nije uspelo. Proverite korisničko ime i lozinku.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}