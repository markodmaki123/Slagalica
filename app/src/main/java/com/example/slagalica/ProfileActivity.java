package com.example.slagalica;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.slagalica.dataBase.DBHelper;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageViewProfile;
    private TextView textViewEmail;
    private TextView textViewUsername;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DBHelper(this);

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewUsername = findViewById(R.id.textViewUsername);

        // Dohvatanje informacija profila iz baze podataka
        String trenutniUser = "Marko"; // Unesite korisničko ime trenutno prijavljenog korisnika

        Cursor cursor = dbHelper.getProfileCursor(trenutniUser);

        if (cursor.moveToFirst()) {
            // Dohvatanje indeksa kolona iz Cursor objekta
            int emailColumnIndex = cursor.getColumnIndexOrThrow("email");
            int usernameColumnIndex = cursor.getColumnIndexOrThrow("username");

            // Dohvatanje vrednosti iz Cursor objekta
            String email = cursor.getString(emailColumnIndex);
            String username = cursor.getString(usernameColumnIndex);

            // Postavljanje vrednosti u TextView elemente iz XML-a
            textViewEmail.setText("Email: " + email);
            textViewUsername.setText("Username: " + username);
        }

        // Zatvaranje Cursor objekta
        cursor.close();

    }
}