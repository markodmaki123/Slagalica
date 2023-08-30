package com.example.slagalica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.slagalica.adapter.GuestResultAdapter;
import com.example.slagalica.dataBase.DBHelper;
import com.example.slagalica.model.GuestResult;

import java.util.List;

public class ResultForGuest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_for_guest);

        DBHelper dbHelper = new DBHelper(this);
        List<GuestResult> resultList = dbHelper.getAllResultsGuest();

// Kreiranje adaptera
        GuestResultAdapter adapter = new GuestResultAdapter(this, resultList);

// Pronađite ListView iz vašeg XML-a
        ListView listView = findViewById(R.id.listViewGuest);

// Postavite adapter na ListView
        listView.setAdapter(adapter);

        Button btnBack = findViewById(R.id.BTNBackGuest);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent homeIntent = new Intent(ResultForGuest.this, LoginActivity.class);
                    startActivity(homeIntent);
                    finish();
            }
        });
    }
}