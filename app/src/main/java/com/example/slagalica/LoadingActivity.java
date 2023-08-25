package com.example.slagalica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoadingActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();

        //databaseReference.child("server").setValue("0");
        databaseReference.child("server").setValue("0");
        databaseReference.child("client").setValue("0");
        databaseReference.child("MojBrojBrojac").setValue("0");


        //zapocni igru
        databaseReference.child("zapocniIgru").setValue("0");

        //restartovanje MojBroj
        databaseReference.child("brojevi").child("brojRestart").setValue("0");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(LoadingActivity.this, LoginActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}