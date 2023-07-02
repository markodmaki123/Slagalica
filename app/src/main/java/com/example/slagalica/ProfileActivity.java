package com.example.slagalica;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.slagalica.dataBase.DBHelper;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageViewProfile;
    private TextView textViewEmail;
    private TextView textViewUsername;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DBHelper(this);

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewUsername = findViewById(R.id.textViewUsername);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);


        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("currentUser", "");

        Cursor cursor = dbHelper.getProfileCursor(currentUser);

        if (cursor.moveToFirst()) {
            int emailColumnIndex = cursor.getColumnIndexOrThrow("email");
            int usernameColumnIndex = cursor.getColumnIndexOrThrow("username");

            String email = cursor.getString(emailColumnIndex);
            String username = cursor.getString(usernameColumnIndex);

            textViewEmail.setText("Email: " + email);
            textViewUsername.setText("Username: " + username);
        }

        cursor.close();

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
                } else if (itemId == R.id.menu_leaderboard) {
                    openLeaderboard();
                } else if (itemId == R.id.menu_logout) {
                    logout();
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

    }

    private void openProfile() {
        // Implementacija otvaranja profila
        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
    private void openHome() {
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void openLeaderboard() {

    }

    private void logout() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}