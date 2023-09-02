package com.example.slagalica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.Manifest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slagalica.dataBase.DBHelper;
import com.example.slagalica.games.MojBrojActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class LoginActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener sensorEventListener;
    private TextView lightValueTextView;

    private DBHelper dbHelper;
    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";

    private final int MY_PERMISSION_REQUEST_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lightValueTextView = findViewById(R.id.lightValueTextView); // Pretpostavljamo da imate TextView za prikaz vrednosti svetlosti

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor != null) {
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float lightValue = event.values[0];
                    lightValueTextView.setText("Svetlost: " + lightValue);
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
        }


        dbHelper = new DBHelper(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();

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

        Button guestResutlButton = findViewById(R.id.guest_result_button);
        guestResutlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(LoginActivity.this, ResultForGuest.class);
                startActivity(gameIntent);
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lightSensor != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }
}