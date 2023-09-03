package com.example.slagalica;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slagalica.adapter.LeaderboardAdapter;
import com.example.slagalica.dataBase.DBHelper;
import com.example.slagalica.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 10001;
    private ImageView imageViewProfile;
    private TextView textViewEmail;
    private TextView textViewUsername;
    private TextView textViewWins;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    private DBHelper dbHelper;
    private String currentUser;
    private String userUuid;

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    uploadImageToFirebase(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewWins = findViewById(R.id.textViewWins);
        imageViewProfile = findViewById(R.id.ivProfile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        if(imageViewProfile.getDrawable() == null) {
            imageViewProfile.setImageResource(R.drawable.default_profile);
        }


        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("currentUser")) {
            currentUser = sharedPreferences.getString("currentUser", "");
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query leaderboardQuery = usersRef.orderByChild("numberOfWins");
        leaderboardQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.email.equals(currentUser)) {
                        userUuid = userSnapshot.getKey();
                        textViewEmail.setText("Email: " + currentUser);
                        textViewUsername.setText("Username: " + user.username);
                        textViewWins.setText("Pobjede: " + user.brojPobjeda);
                        String userProfilePictureUrl = user.getProfilePictureUrl();
                        Picasso.get()
                                .load(userProfilePictureUrl)
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .into(imageViewProfile);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContent.launch("image/*");
                Toast.makeText(ProfileActivity.this,"juju",Toast.LENGTH_LONG);
            }
        });


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

    private void openLeaderboardWins() {
        Intent intent = new Intent(ProfileActivity.this, LeaderboardActivity.class);
        startActivity(intent);
        finish();
    }


    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profilePictureUrl")
                .child(userUuid); // Use the user's unique ID as the filename

        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String profilePictureUrl = uri.toString();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userUuid); // Replace userUid with the actual user's unique ID

                userRef.child("profilePictureUrl").setValue(profilePictureUrl);

            });
        }).addOnFailureListener(e -> {
        });
    }


    private void logout() {
        editor.clear();
        editor.apply();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}