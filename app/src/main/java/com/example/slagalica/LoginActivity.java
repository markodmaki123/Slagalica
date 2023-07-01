package com.example.slagalica;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.slagalica.dataBase.DBHelper;
import com.example.slagalica.games.MojBrojActivity;

import java.io.ByteArrayOutputStream;

public class LoginActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHelper = new DBHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String emailTest = "marko@gmail.com";
        String usernameTest = "123";
        String passwordTest = "123";
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
                    editor.putString("currentUser", username);
                    editor.apply();
                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Prijavljivanje nije uspelo. Proverite korisničko ime i lozinku.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

        Button guestButton = findViewById(R.id.guest_button);
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(LoginActivity.this, MojBrojActivity.class);
                gameIntent.putExtra("user","guest");
                startActivity(gameIntent);
                finish();
            }
        });
    }
}