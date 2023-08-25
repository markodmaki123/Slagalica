package com.example.slagalica;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;


import com.example.slagalica.dataBase.ConnectionService;
import com.example.slagalica.dataBase.NetworkManager;
import com.example.slagalica.games.AsocijacijeActivity;
import com.example.slagalica.games.KoZnaZnaActivity;
import com.example.slagalica.games.KorakPoKorakActivity;
import com.example.slagalica.games.MojBrojActivity;
import com.example.slagalica.games.SkockoActivity;
import com.example.slagalica.games.SpojniceActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HomeActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Button btnZapocni;


    private Handler handler = new Handler();

    private String host = "";
    private String klijent = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnZapocni = findViewById(R.id.BTNzapocniIgru);


        // Postavljanje toggle dugmeta za Navigation Drawer
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, android.R.string.ok, android.R.string.ok);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Obrada klikova na stavke navigacije
                int itemId = item.getItemId();

                if (itemId == R.id.menu_profile) {
                    // Logika za otvaranje profila
                    openProfile();
                } else if (itemId == R.id.menu_home) {
                    openHome();
                } else if (itemId == R.id.menu_leaderboard) {
                    // Logika za otvaranje rang liste
                    openLeaderboard();
                } else if (itemId == R.id.menu_logout) {
                    // Logika za odjavu
                    logout();
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

        btnZapocni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("server").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        if (value.equals("0")) {
                            databaseReference.child("server").setValue("1");
                            databaseReference.child("zapocniIgru").setValue("1");
                            host = "host";
                            btnZapocni.setEnabled(false);
                            return;
                        } else if (value.equals("1")) {
                            databaseReference.child("client").setValue("1");
                            databaseReference.child("zapocniIgru").setValue("2");
                            klijent = "klijent";
                            btnZapocni.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                databaseReference.child("zapocniIgru").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        if (value.equals("2")) {
                            Intent intent = new Intent(HomeActivity.this, MojBrojActivity.class);
                            intent.putExtra("host", host);
                            intent.putExtra("klijent", klijent);
                            startActivity(intent);
                            finish();
                            databaseReference.child("zapocniIgru").setValue("0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });


    }

    private void openProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void openHome() {
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void openLeaderboard() {

    }

    private void logout() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}