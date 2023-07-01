package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slagalica.HomeActivity;
import com.example.slagalica.ProfileActivity;
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

    private TextView timerView;
    private TextView bodoviView;
    private CountDownTimer timer;
    private boolean timerActive = false;
    private long startTimer = 60000;

    private String checkGuest = "guest";
    private String guest;
    private int bodovi;

    private String correctAnswer;

    private int stepCounter;

    private int stepWrong;

    private int stepId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_korak_po_korak);

        databaseHelper = new DBHelper(this);

        stepWrong = 0;

        // Ubacite pitanja u bazu podataka
        databaseHelper.insertSteps("Ima veze sa Savom i Dunavom",
                "Svih devet slova ove rijeci je razlicito",
                "Ima veze sa zdravljem",
                "Moze se odnositi na zivot",
                "Ima svog agenta i svoju premiju",
                "Za vozilo je obavezno",
                "Postoji i CASCO varijanta",
                "osiguranje"
                );

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);


        tvKorak1 = findViewById(R.id.TVKorak1);
        tvKorak2 = findViewById(R.id.TVKorak2);
        tvKorak2.setVisibility(View.INVISIBLE);
        tvKorak3 = findViewById(R.id.TVKorak3);
        tvKorak3.setVisibility(View.INVISIBLE);
        tvKorak4 = findViewById(R.id.TVKorak4);
        tvKorak4.setVisibility(View.INVISIBLE);
        tvKorak5 = findViewById(R.id.TVKorak5);
        tvKorak5.setVisibility(View.INVISIBLE);
        tvKorak6 = findViewById(R.id.TVKorak6);
        tvKorak6.setVisibility(View.INVISIBLE);
        tvKorak7 = findViewById(R.id.TVKorak7);
        tvKorak7.setVisibility(View.INVISIBLE);
        etOdgovor = findViewById(R.id.ETOdgovor1);
        btnKorak = findViewById(R.id.btnKorak);
        timerView = findViewById(R.id.TVTimer);
        bodoviView = findViewById(R.id.TVBodovi);

        bodoviView.setText(String.valueOf(bodovi));


        stepId = 1;
        stepCounter = 1;
        String[] stepDetails = databaseHelper.getStep(stepId);
        correctAnswer = stepDetails[7];
        displaySteps(stepDetails);

        // Dodaj click listenere na odgovore
        btnKorak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(etOdgovor.getText().toString());
            }
        });

        startTimer(startTimer);

    }

    private void displaySteps(String[] stepDetails) {
        // Prikazi pitanje

        String step1 = stepDetails[0];
        String step2 = stepDetails[1];
        String step3 = stepDetails[2];
        String step4 = stepDetails[3];
        String step5 = stepDetails[4];
        String step6 = stepDetails[5];
        String step7 = stepDetails[6];

        tvKorak1.setText(step1);
        tvKorak2.setText(step2);
        tvKorak3.setText(step3);
        tvKorak4.setText(step4);
        tvKorak5.setText(step5);
        tvKorak6.setText(step6);
        tvKorak7.setText(step7);

    }

    private void checkAnswer(String answer) {
        // Proveri da li je odabrani odgovor tačan
        String answer1 = answer.toLowerCase();
        if (answer1.equals(correctAnswer)) {
            tvKorak2.setVisibility(View.VISIBLE);
            tvKorak3.setVisibility(View.VISIBLE);
            tvKorak4.setVisibility(View.VISIBLE);
            tvKorak5.setVisibility(View.VISIBLE);
            tvKorak6.setVisibility(View.VISIBLE);
            tvKorak7.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Čestitamo! "+correctAnswer+" je tačno!", Toast.LENGTH_SHORT).show();
            etOdgovor.setEnabled(false);
            bodovi = bodovi + (20-(stepWrong*2));
            bodoviView.setText(String.valueOf(bodovi));
            timer.cancel();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(KorakPoKorakActivity.this, SpojniceActivity.class);
                    intent.putExtra("user","guest");
                    intent.putExtra("bodovi",bodovi);
                    startActivity(intent);
                    finish();
                }
            }, 2000);

        } else {
            stepCounter++;
            stepWrong++;
            switch (stepCounter) {
                case 2:
                    tvKorak2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    tvKorak3.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    tvKorak4.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    tvKorak5.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    tvKorak6.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    tvKorak7.setVisibility(View.VISIBLE);
                    break;
                default:
                    Toast.makeText(this, "Niste pogodili. Odgovor je "+correctAnswer+".", Toast.LENGTH_SHORT).show();
                    timer.cancel();
                    bodoviView.setText(String.valueOf(bodovi));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(KorakPoKorakActivity.this, SpojniceActivity.class);
                            intent.putExtra("user","guest");
                            intent.putExtra("bodovi",bodovi);
                            startActivity(intent);
                            finish();
                        }
                    }, 2000);
                    break;
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
                Toast.makeText(KorakPoKorakActivity.this, "Vrijeme je isteklo!", Toast.LENGTH_SHORT).show();
                timer.cancel();
                Intent intent = new Intent(KorakPoKorakActivity.this, HomeActivity.class);
                intent.putExtra("user","guest");
                intent.putExtra("bodovi",bodovi);
                startActivity(intent);
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

}