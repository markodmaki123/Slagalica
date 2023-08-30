package com.example.slagalica;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.example.slagalica.games.MojBrojActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Button btnZapocni;

    private String WhoImI = "";

    private ValueEventListener zapocniIgruListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String value = dataSnapshot.getValue(String.class);
            if (value.equals("2")) {
                Intent intent = new Intent(HomeActivity.this, MojBrojActivity.class);
                intent.putExtra("WhoImI", WhoImI);
                startActivity(intent);
                finish();
                databaseReference.child("zapocniIgru").setValue("0");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };


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


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, android.R.string.ok, android.R.string.ok);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_profile) {
                    openProfile();
                } else if (itemId == R.id.menu_home) {
                    openHome();
                } else if (itemId == R.id.menu_leaderboard_wins) {
                    openLeaderboardWins();
                } else if (itemId == R.id.menu_leaderboard_points) {
                    openLeaderboardPoints();
                } else if (itemId == R.id.menu_logout) {
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
                            WhoImI = "host";
                            btnZapocni.setEnabled(false);
                        } else if (value.equals("1")) {
                            databaseReference.child("client").setValue("1");
                            databaseReference.child("zapocniIgru").setValue("2");
                            WhoImI = "klijent";
                            btnZapocni.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                databaseReference.child("zapocniIgru").addValueEventListener(zapocniIgruListener);
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

    private void openLeaderboardWins() {

    }

    private void openLeaderboardPoints() {

    }

    private void logout() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        databaseReference.child("zapocniIgru").removeEventListener(zapocniIgruListener);
        super.onDestroy();
    }

}