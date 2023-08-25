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
    private Button btnZapocni;

    private TextView timerView;
    private TextView bodoviView;
    private CountDownTimer timer;
    private boolean timerActive = false;
    private long startTimer = 70000;

    private String checkGuest = "guest";
    private String guest;
    private int bodovi;
    String zapocniIgru = "";

    private String correctAnswer;

    private int stepCounter;

    private int stepWrong;

    private int stepId;
    Integer value = 0;

    private String host = "";
    private String klijent = "";
    private Integer potez = 1;
    private String brojIgre = "";

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

        if (guest == null) {
            guest = "";
        }

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

        databaseHelper.insertSteps("Ima veze sa Malezijom",
                "Lik je Bate Zivojinovica u filmu Most",
                "To je strip junaka Hobs",
                "Kod Dragane Mirkovic je animiran",
                "Ima svoju vrstu ajkule",
                "Pripada u porodicu velikih macaka",
                "Postoji bengalski i sibirski",
                "tigar"
        );

        bodovi = getIntent().getIntExtra("bodovi", 0);
        if (host == null) {
            host = "";
        }

        if (klijent == null) {
            klijent = "";
        }



        tvKorak1 = findViewById(R.id.TVKorak1);
        tvKorak1.setVisibility(View.INVISIBLE);
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
        btnZapocni = findViewById(R.id.btnZapocni);

        btnKorak.setVisibility(View.INVISIBLE);
        if(!guest.equals(checkGuest)) {
            if (host.equals("klijent")) {
                btnZapocni.setVisibility(View.INVISIBLE);
            }
        }


        databaseReference.child("idIgreKorakPoKorak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                brojIgre = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Toast.makeText(this, "Ja sam" + host + " ,a moja vrijednost klijenta je |||||z" + klijent +"z||||||", Toast.LENGTH_SHORT).show();

        databaseReference.child("zapocniIgru").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                zapocniIgru = value;
                if (value.equals("2")) {
                    if (host.equals("host")) {
                        Intent intent = new Intent(KorakPoKorakActivity.this, KorakPoKorakActivity.class);
                        intent.putExtra("host", "klijent");
                        intent.putExtra("bodovi", bodovi);
                        startActivity(intent);
                        finish();
                        databaseReference.child("zapocniIgru").setValue("0");
                        databaseReference.child("KorakPoKorak").child("BrojKoraka").setValue(0);

                    } else if (host.equals("klijent")) {
                        Intent intent = new Intent(KorakPoKorakActivity.this, SpojniceActivity.class);
                        intent.putExtra("host", "host");
                        intent.putExtra("bodovi", bodovi);
                        startActivity(intent);
                        finish();
                        databaseReference.child("zapocniIgru").setValue("0");
                        databaseReference.child("KorakPoKorak").child("BrojKoraka").setValue(0);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (guest.equals(checkGuest)) {
            stepId = 2;
            stepCounter = 0;
            String[] stepDetails = databaseHelper.getStep(stepId);
            correctAnswer = stepDetails[7];
            displaySteps(stepDetails);
        } else if (host.equals("host")) {
            databaseReference.child("brojacKorakPoKorak").setValue(1);
            //Toast.makeText(this, "HOST", Toast.LENGTH_LONG).show();
            stepId = 1;
            stepCounter = 0;
            String[] stepDetails = databaseHelper.getStep(stepId);
            correctAnswer = stepDetails[7];

            databaseReference.child("idIgreKorakPoKorak").setValue(String.valueOf(stepId));
            displaySteps(stepDetails);

        } else if (host.equals("klijent")) {
            //Toast.makeText(this, "KLIJENT", Toast.LENGTH_LONG).show();
            String[] stepDetails = databaseHelper.getStep(Integer.parseInt(brojIgre));
            correctAnswer = stepDetails[7];
            Toast.makeText(KorakPoKorakActivity.this, brojIgre, Toast.LENGTH_SHORT).show();
            displaySteps(stepDetails);
        }
        btnKorak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guest.equals(checkGuest)) {
                    checkAnswer(etOdgovor.getText().toString());
                } else if (host.equals("klijent")) {
                    checkAnswer(etOdgovor.getText().toString());
                } else if (host.equals("host")) {
                    checkAnswer(etOdgovor.getText().toString());
                    databaseReference.child("KorakPoKorak").child("BrojKoraka").setValue(potez);
                }

            }
        });

        if(!guest.equals(checkGuest)) {
            databaseReference.child("KorakPoKorak").child("BrojKoraka").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    value = dataSnapshot.getValue(Integer.class);
                    int valueInt = value.intValue();
                    if (valueInt == 7 && host.equals("host")) {
                        btnKorak.setVisibility(View.INVISIBLE);
                    } else if (valueInt != 7 && host.equals("klijent")) {
                        btnKorak.setVisibility(View.INVISIBLE);
                        switch (valueInt) {
                            case 1:
                                tvKorak1.setVisibility(View.VISIBLE);
                                break;
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
                                break;
                        }
                    } else if (valueInt == 7 && host.equals("klijent")) {
                        btnKorak.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        btnZapocni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guest.equals(checkGuest)) {
                    startTimer(startTimer);
                    btnKorak.setVisibility(View.VISIBLE);
                    btnZapocni.setVisibility(View.INVISIBLE);
                    stepCounter++;
                    tvKorak1.setVisibility(View.VISIBLE);
                } else if (host.equals("host")) {
                    btnKorak.setVisibility(View.VISIBLE);
                    btnZapocni.setVisibility(View.INVISIBLE);
                    startTimer(startTimer);
                    stepCounter++;
                    tvKorak1.setVisibility(View.VISIBLE);
                    databaseReference.child("KorakPoKorak").child("BrojKoraka").setValue(potez);
                }

            }
        });


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
        if (answer1.equals(correctAnswer) && guest.equals(checkGuest)) {
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
//-------------------------------------------------------------------------------------------------------------- guest
        } else if ((answer1.equals(correctAnswer) && host.equals("host"))) {
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
                    databaseReference.child("zapocniIgru").setValue("2");
                }
            }, 2000);
//-------------------------------------------------------------------------------------------------------------- host
        } else if (answer1.equals(correctAnswer) && host.equals("klijent")) {
            tvKorak2.setVisibility(View.VISIBLE);
            tvKorak3.setVisibility(View.VISIBLE);
            tvKorak4.setVisibility(View.VISIBLE);
            tvKorak5.setVisibility(View.VISIBLE);
            tvKorak6.setVisibility(View.VISIBLE);
            tvKorak7.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Čestitamo! " + correctAnswer + " je tačno!", Toast.LENGTH_SHORT).show();
            etOdgovor.setEnabled(false);
            bodovi = bodovi + 5;
            bodoviView.setText(String.valueOf(bodovi));
            timer.cancel();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    databaseReference.child("zapocniIgru").setValue("2");
                }
            }, 2000);
            //-------------------------------------------------------------------------------------------------------------- klijent

        } else if (host.equals("host")) {
            stepCounter = stepCounter + 1;
            stepWrong++;
            switch (stepCounter) {
                case 2:
                    tvKorak2.setVisibility(View.VISIBLE);
                    potez++;
                    break;
                case 3:
                    tvKorak3.setVisibility(View.VISIBLE);
                    potez++;
                    break;
                case 4:
                    tvKorak4.setVisibility(View.VISIBLE);
                    potez++;
                    break;
                case 5:
                    tvKorak5.setVisibility(View.VISIBLE);
                    potez++;
                    break;
                case 6:
                    tvKorak6.setVisibility(View.VISIBLE);
                    potez++;
                    break;
                case 7:
                    tvKorak7.setVisibility(View.VISIBLE);
                    potez++;
                    break;
                default:
                    Toast.makeText(this, "Niste pogodili. Odgovor je " + correctAnswer + ".", Toast.LENGTH_SHORT).show();
                    timer.cancel();
                    bodoviView.setText(String.valueOf(bodovi));
                    if (zapocniIgru.equals("0")) {
                        databaseReference.child("zapocniIgru").setValue("1");
                    }
                    break;
            }

        } else if (host.equals("klijent")) {
            Toast.makeText(this, "Niste pogodili. Odgovor je " + correctAnswer + ".", Toast.LENGTH_SHORT).show();
            timer.cancel();
            bodoviView.setText(String.valueOf(bodovi));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    databaseReference.child("zapocniIgru").setValue("2");
                }
            }, 2000);

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