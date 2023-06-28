package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import com.example.slagalica.R;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MojBrojActivity extends AppCompatActivity {

    private TextView tvTrazeniBroj;
    private TextView tvJednocifreni1;
    private TextView tvJednocifreni2;
    private TextView tvJednocifreni3;
    private TextView tvJednocifreni4;
    private TextView tvSrednji;
    private TextView tvVeliki;
    private Button btnStop;
    private Button btnStart;
    private EditText etResenje;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_broj);



        tvTrazeniBroj = findViewById(R.id.TVTrazeniBroj);
        tvJednocifreni1 = findViewById(R.id.TVJednocifreni1);
        tvJednocifreni2 = findViewById(R.id.TVJednocifreni2);
        tvJednocifreni3 = findViewById(R.id.TVJednocifreni3);
        tvJednocifreni4 = findViewById(R.id.TVJednocifreni4);
        tvSrednji = findViewById(R.id.TVSrednji);
        tvVeliki = findViewById(R.id.TVVeliki);
        btnStop = findViewById(R.id.BTNStop);
        btnStart = findViewById(R.id.BTNStart);
        etResenje = findViewById(R.id.ETResenje);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generisiBrojeve();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proveriResenje();
            }
        });
        int trazeniBroj = generisiSlucajniBroj();
        tvTrazeniBroj.setText(String.valueOf(trazeniBroj));
    }

    private int generisiSlucajniBroj() {
        // Generisanje slučajnog broja od 1 do 100
        Random random = new Random();
        return random.nextInt(999) + 1;
    }

    private void generisiBrojeve() {
        Random random = new Random();

        // Generisanje prvih četiri jednocifrena broja
        int jednocifreni1 = random.nextInt(9) + 1;
        int jednocifreni2 = random.nextInt(9) + 1;
        int jednocifreni3 = random.nextInt(9) + 1;
        int jednocifreni4 = random.nextInt(9) + 1;

        // Generisanje srednjeg broja (10, 15 ili 20)
        int srednji;
        int srednjiIndex = random.nextInt(3);
        if (srednjiIndex == 0) {
            srednji = 10;
        } else if (srednjiIndex == 1) {
            srednji = 15;
        } else {
            srednji = 20;
        }

        // Generisanje zadnjeg broja (25, 50, 75 ili 100)
        int zadnjiIndex = random.nextInt(4);
        int zadnji;
        switch (zadnjiIndex) {
            case 0:
                zadnji = 25;
                break;
            case 1:
                zadnji = 50;
                break;
            case 2:
                zadnji = 75;
                break;
            default:
                zadnji = 100;
                break;
        }

        // Postavljanje generisanih brojeva u odgovarajuća TextView polja
        tvJednocifreni1.setText(String.valueOf(jednocifreni1));
        tvJednocifreni2.setText(String.valueOf(jednocifreni2));
        tvJednocifreni3.setText(String.valueOf(jednocifreni3));
        tvJednocifreni4.setText(String.valueOf(jednocifreni4));
        tvSrednji.setText(String.valueOf(srednji));
        tvVeliki.setText(String.valueOf(zadnji));
    }

    private int izracunajKompleksniIzraz(String izraz) {

        try {
            Expression expression = new ExpressionBuilder(izraz).build();
            double result = expression.evaluate();
            return (int) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void proveriResenje() {
        // Provjeri da li su svi uneseni brojevi prisutni u ponuđenim brojevima

            String unesenoResenje = etResenje.getText().toString();
            int trazeniBroj = Integer.parseInt(tvTrazeniBroj.getText().toString());

            // Provera tačnosti rešenja
            int rezultat = izracunajKompleksniIzraz(unesenoResenje);
            boolean tacno = rezultat == trazeniBroj;

        // Provera da li su brojevi u rešenju prisutni u ponuđenim brojevima
        List<Integer> ponudjeniBrojevi = new ArrayList<>();
        ponudjeniBrojevi.add(Integer.parseInt(tvJednocifreni1.getText().toString()));
        ponudjeniBrojevi.add(Integer.parseInt(tvJednocifreni2.getText().toString()));
        ponudjeniBrojevi.add(Integer.parseInt(tvJednocifreni3.getText().toString()));
        ponudjeniBrojevi.add(Integer.parseInt(tvJednocifreni4.getText().toString()));
        ponudjeniBrojevi.add(Integer.parseInt(tvSrednji.getText().toString()));
        ponudjeniBrojevi.add(Integer.parseInt(tvVeliki.getText().toString()));

        boolean brojeviPostoje = true;
        String[] brojeviIzraza = unesenoResenje.split("[+\\-*/()]");
        for (String brojStr : brojeviIzraza) {
            if (!brojStr.isEmpty()) {
                int broj = Integer.parseInt(brojStr);
                if (!ponudjeniBrojevi.contains(broj)) {
                    brojeviPostoje = false;
                    break;
                }
            }
        }

        // Prikazivanje rezultata korisniku
        if (tacno && brojeviPostoje) {
            Toast.makeText(this, "Čestitamo! Rešenje je tačno!", Toast.LENGTH_SHORT).show();
        } else if (!tacno) {
            Toast.makeText(this, "Rešenje nije tačno. Pokušaj ponovo.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Neki brojevi u rešenju ne postoje među ponuđenim brojevima.", Toast.LENGTH_SHORT).show();
        }
    }
}