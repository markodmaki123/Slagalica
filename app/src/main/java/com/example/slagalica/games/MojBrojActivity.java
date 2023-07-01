package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import com.example.slagalica.LoginActivity;
import com.example.slagalica.R;
import com.example.slagalica.dataBase.ConnectionService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MojBrojActivity extends AppCompatActivity {

    private ConnectionService connectionService;
    private boolean isBound = false;

    private TextView tvTrazeniBroj;
    private TextView tvJednocifreni1;
    private TextView tvJednocifreni2;
    private TextView tvJednocifreni3;
    private TextView tvJednocifreni4;
    private TextView tvSrednji;
    private TextView tvVeliki;

    private TextView timerView;
    private TextView bodoviView;
    private CountDownTimer timer;
    private boolean timerActive = false;
    private long startTimer = 60000;

    private Button btnStop;
    private Button btnStart;
    private String checkGuest = "guest";
    private String guest;
    private int bodovi;

    private Button btnReceive;
    private EditText etResenje;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectionService.ConnectionServiceBinder binder = (ConnectionService.ConnectionServiceBinder) service;
            connectionService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connectionService = null;
            isBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_broj);

        guest = getIntent().getStringExtra("user");

        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);


        tvTrazeniBroj = findViewById(R.id.TVTrazeniBroj);
        timerView = findViewById(R.id.TVTimer);
        bodoviView = findViewById(R.id.TVBodovi);
        tvJednocifreni1 = findViewById(R.id.TVJednocifreni1);
        tvJednocifreni2 = findViewById(R.id.TVJednocifreni2);
        tvJednocifreni3 = findViewById(R.id.TVJednocifreni3);
        tvJednocifreni4 = findViewById(R.id.TVJednocifreni4);
        tvSrednji = findViewById(R.id.TVSrednji);
        tvVeliki = findViewById(R.id.TVVeliki);
        btnStop = findViewById(R.id.BTNStop);
        btnReceive = findViewById(R.id.BTNReceive);
        btnStart = findViewById(R.id.BTNStart);
        etResenje = findViewById(R.id.ETResenje);

        boolean isServer = getIntent().getBooleanExtra("IS_SERVER", false);
        if(!guest.equals(checkGuest)) {
        if (!isServer) {
            btnStop.setVisibility(View.INVISIBLE);
        } else {
            btnReceive.setVisibility(View.INVISIBLE);
        }
        }

        if(guest.equals(checkGuest)) {
            btnReceive.setVisibility(View.INVISIBLE);
        }

        bodoviView.setText("0");

        /*boolean isHost = isHost();
        if (isHost) {
            connectionService.connectToServerMessage(); //klijent
        } else {
             //server

        }*/



        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStop.setVisibility(View.INVISIBLE);
                startTimer(startTimer);
                generisiBrojeve();
                // Prepare the GUI state as a Map<String, String>
                if(!guest.equals(checkGuest)) {
                    if (isServer) {
                        Map<String, String> guiState = new HashMap<>();
                        guiState.put("jednocifreni1", tvJednocifreni1.getText().toString());
                        guiState.put("jednocifreni2", tvJednocifreni2.getText().toString());
                        guiState.put("jednocifreni3", tvJednocifreni3.getText().toString());
                        guiState.put("jednocifreni4", tvJednocifreni4.getText().toString());
                        guiState.put("srednji", tvSrednji.getText().toString());
                        guiState.put("veliki", tvVeliki.getText().toString());
                        guiState.put("trazeniBroj", tvTrazeniBroj.getText().toString());

                        // Send the GUI state to the client
                        if (connectionService != null) {
                            //connectionService.sendMessage(guiState);
                        }
                    }
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proveriResenje();
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGUI();
            }
        });

    }

    private int generisiSlucajniBroj() {
        // Generisanje slučajnog broja od 1 do 100
        Random random = new Random();
        return random.nextInt(999) + 1;

    }

    private void generisiBrojeve() {
        int trazeniBroj = generisiSlucajniBroj();
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
        tvTrazeniBroj.setText(String.valueOf(trazeniBroj));
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
            bodovi = bodovi + 20;
            if(guest.equals(checkGuest)) {
                timer.cancel();
                Intent gameIntent = new Intent(MojBrojActivity.this,KoZnaZnaActivity.class);
                gameIntent.putExtra("user","guest");
                gameIntent.putExtra("bodovi",bodovi);
                startActivity(gameIntent);
                finish();
            }
        } else if (!tacno) {
            int polovicniRezultat =rezultat-trazeniBroj ;
            Toast.makeText(this, "Vase rešenje je za "+polovicniRezultat+" udaljeno od tacnog.", Toast.LENGTH_SHORT).show();
            if (polovicniRezultat >= -25 && polovicniRezultat <= 25) {
                bodovi=bodovi+10;
                if(guest.equals(checkGuest)) {
                    timer.cancel();
                    Intent gameIntent = new Intent(MojBrojActivity.this,KoZnaZnaActivity.class);
                    gameIntent.putExtra("user","guest");
                    gameIntent.putExtra("bodovi",bodovi);
                    startActivity(gameIntent);
                    finish();
                }
            }
            if(guest.equals(checkGuest) && !timerActive) {
                timer.cancel();
                Intent gameIntent = new Intent(MojBrojActivity.this,KoZnaZnaActivity.class);
                gameIntent.putExtra("user","guest");
                gameIntent.putExtra("bodovi",bodovi);
                startActivity(gameIntent);
                finish();
            }
        } else {
            Toast.makeText(this, "Neki brojevi u rešenju ne postoje među ponuđenim brojevima.", Toast.LENGTH_SHORT).show();
            if(guest.equals(checkGuest) && !timerActive) {
                timer.cancel();
                Intent gameIntent = new Intent(MojBrojActivity.this,KoZnaZnaActivity.class);
                gameIntent.putExtra("user","guest");
                gameIntent.putExtra("bodovi",bodovi);
                startActivity(gameIntent);
                finish();
            }
        }
    }

    private void startTimer(long time) {
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timerView.setText("00");
                timerActive = false;
                Toast.makeText(MojBrojActivity.this, "Vrijeme je isteklo!", Toast.LENGTH_SHORT).show();
                proveriResenje();
                finish();
            }
        };

        timer.start();
        timerActive = true;
    }

    private void updateTimerText(long millisUntilFinished) {
        int seconds = (int) (millisUntilFinished / 1000);

        String time = String.format("%02d", seconds);
        timerView.setText(time);
    }

    public void updateGUI() {
        Map<String, String> guiState = connectionService.getMessageMap();

        tvJednocifreni1.setText(guiState.get("jednocifreni1"));
        tvJednocifreni2.setText(guiState.get("jednocifreni2"));
        tvJednocifreni3.setText(guiState.get("jednocifreni3"));
        tvJednocifreni4.setText(guiState.get("jednocifreni4"));
        tvSrednji.setText(guiState.get("srednji"));
        tvVeliki.setText(guiState.get("veliki"));
        tvTrazeniBroj.setText(guiState.get("trazeniBroj"));
    }

    private boolean isHost() {

        boolean isServer = getIntent().getBooleanExtra("IS_SERVER", false);

        if (!isServer) {
            try {
                final int timeout = 3000; // 5000 milisekundi (5 sekundi)
                final int port = 1234; // Odaberite prikladni port

                AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        try {
                            Socket testSocket = new Socket();
                            testSocket.connect(new InetSocketAddress("192.168.0.12", port), timeout); // Zamijenite sa stvarnom IP adresom domaćina (servera)
                            testSocket.close();
                            return true;
                        } catch (IOException e) {
                            return false;
                        }
                    }
                };
                return task.execute().get(); // Pokreće se asinkroni zadatak i čeka se rezultat
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }

    }
}