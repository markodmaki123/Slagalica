package com.example.slagalica;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.slagalica.games.MojBrojActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Button btnZapocni;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences1;

    private String WhoImI = "";
    private String igrac1="";
    private String igrac2="";

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

        DatabaseReference igraRef = FirebaseDatabase.getInstance().getReference("igra");



        SharedPreferences[] sharedPreferences = {getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)};
        String email = sharedPreferences[0].getString("currentUser","");

        sharedPreferences1 = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences1.edit();

        igraRef.child("igrac2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    igrac2 = dataSnapshot.getValue(String.class);
                    if(igrac2!=null || igrac2.equals("")) {
                        editor.putString("mojProtivnik2", igrac2);
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Došlo je do greške pri čitanju iz baze
            }
        });

        igraRef.child("igrac1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    igrac1 = dataSnapshot.getValue(String.class);
                    if(igrac1!=null || igrac1.equals("")) {
                        editor.putString("mojProtivnik1", igrac1);
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Došlo je do greške pri čitanju iz baze
            }
        });

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersRef.orderByChild("email").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String username = userSnapshot.child("username").getValue(String.class);
                        if (username != null) {
                            editor.putString("currentUserUsername", username);
                            editor.apply();
                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Došlo je do greške pri proveri e-maila.", Toast.LENGTH_SHORT).show();
            }
        });


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
                            String currentUserUsername = sharedPreferences1.getString("currentUserUsername", "");
                            databaseReference.child("igra").child("igrac1").setValue(currentUserUsername);
                            btnZapocni.setEnabled(false);
                        } else if (value.equals("1")) {
                            databaseReference.child("client").setValue("1");
                            databaseReference.child("zapocniIgru").setValue("2");
                            WhoImI = "klijent";
                            String currentUserUsername = sharedPreferences1.getString("currentUserUsername", "");
                            databaseReference.child("igra").child("igrac2").setValue(currentUserUsername);
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
        Intent intent = new Intent(HomeActivity.this, LeaderboardActivity.class);
        startActivity(intent);
        finish();
    }


    private void logout() {
        editor.clear();
        editor.apply();
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