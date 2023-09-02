package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KorakPoKorakActivity extends AppCompatActivity {
    private DBHelper databaseHelper;
    private List<CountDownTimer> activeTimers = new ArrayList<>();

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
    private TextView tvProtinik;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private TextView timerView;
    private TextView bodoviView;
    private CountDownTimer timer;
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

    private String WhoImI="";
    private Integer potez = 1;
    private String brojIgre = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_korak_po_korak);

        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();

        databaseReference.child("KorakPoKorak").child("BrojKoraka").setValue(0);

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);
        WhoImI = getIntent().getStringExtra("WhoImI");
        databaseHelper = new DBHelper(this);

        if (guest == null) {
            guest = "";
        }
        if (WhoImI == null) {
            WhoImI = "";
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

        databaseHelper.insertSteps("Pominje se u Bajaginom pesmi",
                "Ime je rijeke u Briselu",
                "Ime fudbalera Markosa",
                "Ime glumice Djorovic",
                "Pariz lezi na toj rijeci",
                "Legenda je formule 1",
                "Obicno se pojavi na suncanom danu iza nas",
                "sena"
        );

        databaseHelper.insertSteps("U filmu moze da leti",
                "Moze imati veze sa zivotom",
                "Moze imati veze sa morem",
                "U domacem filmu je Prokleti",
                "U domacem filmu je Voleo Vozove",
                "Obicno zivi 7 puta manje nego covjek",
                "To je covjek najbolji prijatelj",
                "pas"
        );

        bodovi = getIntent().getIntExtra("bodovi", 0);

        tvProtinik = findViewById(R.id.TVProtivnik);
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

        String protivnik = sharedPreferences.getString("mojProtivnik", "");

        tvProtinik.setText("VS " + protivnik);

        btnKorak.setVisibility(View.INVISIBLE);

        if(!guest.equals(checkGuest)) {
            if (WhoImI.equals("klijent")) {
                btnZapocni.setVisibility(View.INVISIBLE);
            }
        }


        databaseReference.child("zapocniIgru").addValueEventListener(prebacivajeIgre);

        if (guest.equals(checkGuest)) {
            Random random = new Random();
            stepId = random.nextInt(3) + 1;
            stepCounter = 0;
            String[] stepDetails = databaseHelper.getStep(stepId);
            correctAnswer = stepDetails[7];
            displaySteps(stepDetails);
        } else if (WhoImI.equals("host")) {
            databaseReference.child("brojacKorakPoKorak").setValue(1);
            Random random = new Random();
            stepId = random.nextInt(3) + 1;
            stepCounter = 0;
            String[] stepDetails = databaseHelper.getStep(stepId);
            correctAnswer = stepDetails[7];

            databaseReference.child("idIgreKorakPoKorak").setValue(stepId);
            displaySteps(stepDetails);

        } else if (WhoImI.equals("klijent")) {
            databaseReference.child("idIgreKorakPoKorak").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Integer value = dataSnapshot.getValue(Integer.class);
                    String[] stepDetails = databaseHelper.getStep(value);
                    correctAnswer = stepDetails[7];
                    displaySteps(stepDetails);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        btnKorak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guest.equals(checkGuest)) {
                    checkAnswer(etOdgovor.getText().toString());
                } else if (WhoImI.equals("klijent")) {
                    checkAnswer(etOdgovor.getText().toString());
                } else if (WhoImI.equals("host")) {
                    checkAnswer(etOdgovor.getText().toString());
                    databaseReference.child("KorakPoKorak").child("BrojKoraka").setValue(potez);
                }

            }
        });

        if(!guest.equals(checkGuest)) {
            Toast.makeText(this, "Vasa uloga u ovoj partiji je |"+ WhoImI + "|", Toast.LENGTH_SHORT).show();
            databaseReference.child("KorakPoKorak").child("BrojKoraka").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    value = dataSnapshot.getValue(Integer.class);
                    int valueInt = value.intValue();
                    if (valueInt == 8 && WhoImI.equals("host")) {
                        btnKorak.setVisibility(View.INVISIBLE);
                    } else if (valueInt != 8 && WhoImI.equals("klijent")) {
                        btnKorak.setVisibility(View.INVISIBLE);
                        switch (valueInt) {
                            case 1:
                                startTimer(startTimer);
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
                    } else if (valueInt == 8 && WhoImI.equals("klijent")) {
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
                } else if (WhoImI.equals("host")) {
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
        } else if ((answer1.equals(correctAnswer) && WhoImI.equals("host"))) {
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
                    if (zapocniIgru.equals("0")) {
                        databaseReference.child("zapocniIgru").setValue("1");
//                        Intent gameIntent = new Intent(KorakPoKorakActivity.this, KorakPoKorakActivity.class);
//                        gameIntent.putExtra("WhoImI", "klijent");
//                        gameIntent.putExtra("bodovi", bodovi);
//                        startActivity(gameIntent);
//                        finish();
                    } else if (zapocniIgru.equals("1.5")) {
                        databaseReference.child("zapocniIgru").setValue("2");
                    }
                }
            }, 2000);
//-------------------------------------------------------------------------------------------------------------- host
        } else if (answer1.equals(correctAnswer) && WhoImI.equals("klijent")) {
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
                    if (zapocniIgru.equals("0")) {
                        databaseReference.child("zapocniIgru").setValue("1");
//                        Intent gameIntent = new Intent(KorakPoKorakActivity.this, KorakPoKorakActivity.class);
//                        gameIntent.putExtra("WhoImI", "host");
//                        gameIntent.putExtra("bodovi", bodovi);
//                        startActivity(gameIntent);
//                        finish();
                    } else if (zapocniIgru.equals("1.5")) {
                        databaseReference.child("zapocniIgru").setValue("2");
                    }
                }
            }, 2000);
            //-------------------------------------------------------------------------------------------------------------- klijent

        } else if (WhoImI.equals("host")) {
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
                    potez++;
                    Toast.makeText(this, "Niste pogodili. Odgovor je " + correctAnswer + ".", Toast.LENGTH_SHORT).show();
                    timer.cancel();
                    bodoviView.setText(String.valueOf(bodovi));
                    break;
            }

        } else if (WhoImI.equals("klijent")) {
            Toast.makeText(this, "Niste pogodili. Odgovor je " + correctAnswer + ".", Toast.LENGTH_SHORT).show();
            timer.cancel();
            bodoviView.setText(String.valueOf(bodovi));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (zapocniIgru.equals("0")) {
                        databaseReference.child("zapocniIgru").setValue("1");
//                        Intent gameIntent = new Intent(KorakPoKorakActivity.this, KorakPoKorakActivity.class);
//                        gameIntent.putExtra("WhoImI", "host");
//                        gameIntent.putExtra("bodovi", bodovi);
//                        startActivity(gameIntent);
//                        finish();
                    } else if (zapocniIgru.equals("1.5")) {
                        databaseReference.child("zapocniIgru").setValue("2");
                    }
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
                activeTimers.remove(this);
            }
        };

        timer.start();
        activeTimers.add(timer);
    }

    private void updateTimerText(long millisUntilFinished) {
        int seconds = (int) (millisUntilFinished / 1000);

        String time = String.format("%02d", seconds);
        timerView.setText(time);
    }

    @Override
    protected void onDestroy() {
        stopAllTimers();
        databaseReference.child("zapocniIgru").removeEventListener(prebacivajeIgre);
        super.onDestroy();
    }

    private ValueEventListener prebacivajeIgre = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String value = dataSnapshot.getValue(String.class);
            zapocniIgru = value;
            if (value.equals("2")) {
                Intent intent = new Intent(KorakPoKorakActivity.this, SpojniceActivity.class);
                intent.putExtra("bodovi", bodovi);
                if (WhoImI.equals("host")) {
                    intent.putExtra("WhoImI", "klijent");
                } else if (WhoImI.equals("klijent")) {
                    intent.putExtra("WhoImI", "host");
                }
                startActivity(intent);
                finish();
                databaseReference.child("zapocniIgru").setValue("0");
            } else if (value.equals("1")) {
                Intent intent = new Intent(KorakPoKorakActivity.this, KorakPoKorakActivity.class);
                intent.putExtra("bodovi", bodovi);
                databaseReference.child("zapocniIgru").setValue("1.5");
                if (WhoImI.equals("host")) {
                    intent.putExtra("WhoImI", "klijent");
                } else if (WhoImI.equals("klijent")) {
                    if(potez!=8) {
                        tvKorak2.setVisibility(View.VISIBLE);
                        tvKorak3.setVisibility(View.VISIBLE);
                        tvKorak4.setVisibility(View.VISIBLE);
                        tvKorak5.setVisibility(View.VISIBLE);
                        tvKorak6.setVisibility(View.VISIBLE);
                        tvKorak7.setVisibility(View.VISIBLE);
                        Toast.makeText(KorakPoKorakActivity.this, "Tačan odgovor je " + correctAnswer + ".", Toast.LENGTH_SHORT).show();
                    }
                    intent.putExtra("WhoImI", "host");
                }
                startActivity(intent);
                finish();
            }
        }



        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };
    private void stopAllTimers() {
        for (CountDownTimer timer : activeTimers) {
            timer.cancel();
        }
        activeTimers.clear(); // Očisti listu aktivnih tajmera
    }

}