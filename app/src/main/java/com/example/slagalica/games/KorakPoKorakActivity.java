package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.slagalica.R;
import com.example.slagalica.dataBase.DBHelper;

public class KorakPoKorakActivity extends AppCompatActivity {
    private DBHelper databaseHelper;


    private TextView tvKorak1;
    private TextView tvKorak2;
    private TextView tvKorak3;
    private TextView tvKorak4;
    private TextView tvKorak5;
    private TextView tvKorak6;
    private TextView tvKorak7;
    private EditText etOdgovor;
    private Button btnKorak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_korak_po_korak);

        tvKorak1 = findViewById(R.id.TVKorak1);
        tvKorak2 = findViewById(R.id.TVKorak2);
        tvKorak3 = findViewById(R.id.TVKorak3);
        tvKorak4 = findViewById(R.id.TVKorak4);
        tvKorak5 = findViewById(R.id.TVKorak5);
        tvKorak6 = findViewById(R.id.TVKorak6);
        tvKorak7 = findViewById(R.id.TVKorak7);
        etOdgovor = findViewById(R.id.ETOdgovor1);
        btnKorak = findViewById(R.id.btnKorak);
    }
}