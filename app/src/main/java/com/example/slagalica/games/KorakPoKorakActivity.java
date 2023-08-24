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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class KorakPoKorakActivity extends AppCompatActivity {
    private DBHelper databaseHelper;

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";

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

    private String host = "";
    private String klijent = "";
    private int potez = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_korak_po_korak);

        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);
        host = getIntent().getStringExtra("host");
        klijent = getIntent().getStringExtra("klijent");
        databaseHelper = new DBHelper(this);

        stepWrong = 0;
        databaseHelper.insertSteps("Ima veze sa Savom i Dunavom",
                "Svih devet slova ove rijeci je razlicito",
                "Ima veze sa zdravljem",
                "Moze se odnositi na zivot",
                "Ima svog agenta i svoju premiju",
                "Za vozilo je obavezno",
                "Postoji i CASCO varijanta",
                "osiguranje"
        );

        bodovi = getIntent().getIntExtra("bodovi", 0);
        if (guest == null) {
            guest = "";
        }


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

        databaseReference.child("brojacKorakPoKorak").setValue(1);

        databaseReference.child("brojacKorakPoKorak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                potez = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (guest.equals(checkGuest)) {
            stepId = 1;
            stepCounter = 1;
            String[] stepDetails = databaseHelper.getStep(stepId);
            correctAnswer = stepDetails[7];
            displaySteps(stepDetails);
        } else if (host.equals("klijent")) {
            Toast.makeText(this, "HOST", Toast.LENGTH_LONG).show();
            stepId = 1;
            stepCounter = 1;
            String[] stepDetails = databaseHelper.getStep(stepId);
            correctAnswer = stepDetails[7];

            databaseReference.child("idIgreKorakPoKorak").setValue(String.valueOf(stepId));
            displaySteps(stepDetails);
            databaseReference.child("brojacKorakPoKorak").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    potez = dataSnapshot.getValue(Integer.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } else if (klijent.equals("host")) {
            Toast.makeText(this, "KLIJENT", Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    databaseReference.child("idIgreKorakPoKorak").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String idIgreKorakPoKorak = dataSnapshot.getValue(String.class);
                            String[] stepDetails = databaseHelper.getStep(Integer.parseInt(idIgreKorakPoKorak));
                            Toast.makeText(KorakPoKorakActivity.this, idIgreKorakPoKorak, Toast.LENGTH_SHORT).show();
                            displaySteps(stepDetails);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }, 10000);
        }
        btnKorak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guest.equals(checkGuest)) {
                    checkAnswer(etOdgovor.getText().toString());
                } else if (klijent.equals("klijent") && (potez % 2 == 0)) {
                    potez++;
                    databaseReference.child("brojacKorakPoKorak").setValue(potez);
                    checkAnswer(etOdgovor.getText().toString());
                } else if (host.equals("host") && (potez % 2 == 1)) {
                    potez++;
                    databaseReference.child("brojacKorakPoKorak").setValue(potez);
                    checkAnswer(etOdgovor.getText().toString());
                }

            }
        });

        startTimer(startTimer);

    }

    private void displaySteps(String[] stepDetails) {
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
        String answer1 = answer.toLowerCase();
        if (answer1.equals(correctAnswer)) {
            tvKorak2.setVisibility(View.VISIBLE);
            tvKorak3.setVisibility(View.VISIBLE);
            tvKorak4.setVisibility(View.VISIBLE);
            tvKorak5.setVisibility(View.VISIBLE);
            tvKorak6.setVisibility(View.VISIBLE);
            tvKorak7.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Čestitamo! " + correctAnswer + " je tačno!", Toast.LENGTH_SHORT).show();
            etOdgovor.setEnabled(false);
            bodovi = bodovi + (20 - (stepWrong * 2));
            bodoviView.setText(String.valueOf(bodovi));
            timer.cancel();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(KorakPoKorakActivity.this, SpojniceActivity.class);
                    intent.putExtra("user", "guest");
                    intent.putExtra("bodovi", bodovi);
                    startActivity(intent);
                    finish();
                }
            }, 2000);

        } else if (!guest.equals(checkGuest)) {
            stepCounter = stepCounter + 1;
            stepWrong++;
            switch (potez) {
                case 3:
                    tvKorak2.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    tvKorak3.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    tvKorak4.setVisibility(View.VISIBLE);
                    break;
                case 9:
                    tvKorak5.setVisibility(View.VISIBLE);
                    break;
                case 11:
                    tvKorak6.setVisibility(View.VISIBLE);
                    break;
                case 13:
                    tvKorak7.setVisibility(View.VISIBLE);
                    break;
                default:
                    if (potez == 15) {
                        Toast.makeText(this, "Niste pogodili. Odgovor je " + correctAnswer + ".", Toast.LENGTH_SHORT).show();
                        timer.cancel();
                        bodoviView.setText(String.valueOf(bodovi));
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(KorakPoKorakActivity.this, SpojniceActivity.class);
                                intent.putExtra("user", "guest");
                                intent.putExtra("bodovi", bodovi);
                                startActivity(intent);
                                finish();
                            }
                        }, 2000);
                        break;
                    } else {
                        break;
                    }
            }
        } else {
            stepCounter = stepCounter + 1;
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
                    Toast.makeText(this, "Niste pogodili. Odgovor je " + correctAnswer + ".", Toast.LENGTH_SHORT).show();
                    timer.cancel();
                    bodoviView.setText(String.valueOf(bodovi));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(KorakPoKorakActivity.this, SpojniceActivity.class);
                            intent.putExtra("user", "guest");
                            intent.putExtra("bodovi", bodovi);
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
                intent.putExtra("user", "guest");
                intent.putExtra("bodovi", bodovi);
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