package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import com.example.slagalica.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MojBrojActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private SensorEventListener sensorEventListener;
    private boolean isShaken = false;

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";


    private TextView tvTrazeniBroj;
    private TextView tvJednocifreni1;
    private TextView tvJednocifreni2;
    private TextView tvJednocifreni3;
    private TextView tvJednocifreni4;
    private TextView tvSrednji;
    private TextView tvVeliki;
    private TextView timerView;
    private TextView bodoviView;
    private TextView tvProtinik;

    private CountDownTimer timer;
    private long timerTime = 60000;

    private Button btnStop;
    private Button btnKonacnoMojBroj;


    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private String checkGuest = "guest";
    private String guest;

    private String WhoImI = "";
    private String zapocniIgru = "";
    private Boolean resenjeServera;
    private Boolean resenjePoslano;

    private List<CountDownTimer> activeTimers = new ArrayList<>();

    private int bodovi = 0;
    private EditText etResenje;

    private ValueEventListener brojevi = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String brojRestart = dataSnapshot.child("brojRestart").getValue(String.class);
            String trazeni = dataSnapshot.child("trazeni").getValue(String.class);
            String broj1 = dataSnapshot.child("broj1").getValue(String.class);
            String broj2 = dataSnapshot.child("broj2").getValue(String.class);
            String broj3 = dataSnapshot.child("broj3").getValue(String.class);
            String broj4 = dataSnapshot.child("broj4").getValue(String.class);
            String srednji = dataSnapshot.child("srednji").getValue(String.class);
            String veliki = dataSnapshot.child("veliki").getValue(String.class);

            String protivnik = sharedPreferences.getString("mojProtivnik", "");

            tvProtinik.setText("VS " + protivnik);
            tvTrazeniBroj.setText(trazeni);
            tvJednocifreni1.setText(broj1);
            tvJednocifreni2.setText(broj2);
            tvJednocifreni3.setText(broj3);
            tvJednocifreni4.setText(broj4);
            tvSrednji.setText(srednji);
            tvVeliki.setText(veliki);
            if (brojRestart.equals("1")) {
                startTimer(timerTime);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };


    private ValueEventListener zapocniIgruListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String value = dataSnapshot.getValue(String.class);
            zapocniIgru = value;
            if (zapocniIgru.equals("2")) {
                Intent intent = new Intent(MojBrojActivity.this, KoZnaZnaActivity.class);
                intent.putExtra("bodovi", bodovi);
                if (WhoImI.equals("host")) {
                    intent.putExtra("WhoImI", "klijent");
                } else if (WhoImI.equals("klijent")) {
                    intent.putExtra("WhoImI", "host");
                }
                stopAllTimers();
                startActivity(intent);
                finish();
                databaseReference.child("zapocniIgru").setValue("0");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_broj);

        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);
        WhoImI = getIntent().getStringExtra("WhoImI");

        //null point ex
        if (guest == null) {
            guest = "";
        }
        if (WhoImI == null) {
            WhoImI = "";
        }

        tvProtinik = findViewById(R.id.TVProtivnik);
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
        btnKonacnoMojBroj = findViewById(R.id.BTNKonacnoMojBroj);
        etResenje = findViewById(R.id.ETResenje);

        databaseReference.child("brojevi").child("brojRestart").setValue("0");

        if (!guest.equals(checkGuest)) {
            if (WhoImI.equals("klijent")) {
                if(!sharedPreferences.contains("mojProtivnik")) {
                    String protivnik = sharedPreferences.getString("mojProtivnik1", "");
                    editor.putString("mojProtivnik", protivnik);
                    editor.apply();
                }
                btnStop.setVisibility(View.INVISIBLE);
            } else if (WhoImI.equals("host")) {
                if(!sharedPreferences.contains("mojProtivnik")) {
                    String protivnik = sharedPreferences.getString("mojProtivnik2", "");
                    editor.putString("mojProtivnik", protivnik);
                    editor.apply();
                }
            }
        }

        databaseReference.child("brojevi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resenjeServera = dataSnapshot.child("resenjeServera").getValue(Boolean.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        databaseReference.child("brojevi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resenjePoslano = dataSnapshot.child("resenjePoslano").getValue(Boolean.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        databaseReference.child("zapocniIgru").addValueEventListener(zapocniIgruListener);

        bodoviView.setText(String.valueOf(bodovi));

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStop.setVisibility(View.INVISIBLE);
                startTimer(timerTime);
                generisiBrojeve();
                if (!guest.equals(checkGuest)) {
                    String protivnik = sharedPreferences.getString("mojProtivnik", "");

                    tvProtinik.setText("VS " + protivnik);

                    databaseReference.child("brojevi").child("resenjeServera").setValue(false);
                    databaseReference.child("brojevi").child("resenjePoslano").setValue(false);
                    databaseReference.child("brojevi").child("trazeni").setValue(tvTrazeniBroj.getText().toString());
                    databaseReference.child("brojevi").child("broj1").setValue(tvJednocifreni1.getText().toString());
                    databaseReference.child("brojevi").child("broj2").setValue(tvJednocifreni2.getText().toString());
                    databaseReference.child("brojevi").child("broj3").setValue(tvJednocifreni3.getText().toString());
                    databaseReference.child("brojevi").child("broj4").setValue(tvJednocifreni4.getText().toString());
                    databaseReference.child("brojevi").child("srednji").setValue(tvSrednji.getText().toString());
                    databaseReference.child("brojevi").child("veliki").setValue(tvVeliki.getText().toString());
                    databaseReference.child("brojevi").child("brojRestart").setValue("1");
                }
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometerSensor != null) {
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    // Detektujte podrhtavanje uređaja (možete prilagoditi prag)
                    if (!isShaken && Math.sqrt(x * x + y * y + z * z) > 15 && WhoImI.equals("host")) {
                        startTimer(timerTime);
                        generisiBrojeve();
                        if (!guest.equals(checkGuest)) {
                            databaseReference.child("brojevi").child("resenjeServera").setValue(false);
                            databaseReference.child("brojevi").child("resenjePoslano").setValue(false);


                            databaseReference.child("brojevi").child("trazeni").setValue(tvTrazeniBroj.getText().toString());
                            databaseReference.child("brojevi").child("broj1").setValue(tvJednocifreni1.getText().toString());
                            databaseReference.child("brojevi").child("broj2").setValue(tvJednocifreni2.getText().toString());
                            databaseReference.child("brojevi").child("broj3").setValue(tvJednocifreni3.getText().toString());
                            databaseReference.child("brojevi").child("broj4").setValue(tvJednocifreni4.getText().toString());
                            databaseReference.child("brojevi").child("srednji").setValue(tvSrednji.getText().toString());
                            databaseReference.child("brojevi").child("veliki").setValue(tvVeliki.getText().toString());
                            databaseReference.child("brojevi").child("brojRestart").setValue("1");
                        }
                        btnStop.setVisibility(View.INVISIBLE);
                        isShaken = true;
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Ne morate implementirati ovo za sejker senzor
                }
            };
        }

        if (guest == "") {
            if (WhoImI.equals("klijent")) {
                databaseReference.child("brojevi").addValueEventListener(brojevi);
            }
        }
        btnKonacnoMojBroj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAllTimers();
                proveriResenje();
            }
        });

    }


    private int generisiSlucajniBroj() {
        Random random = new Random();
        return 5;

    }

    private void generisiBrojeve() {
        int trazeniBroj = generisiSlucajniBroj();
        Random random = new Random();

        // Generisanje prvih četiri jednocifrena broja
        //int jednocifreni1 = random.nextInt(9) + 1; da bude 5
        int jednocifreni1 = 5;
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

        int rezultat = izracunajKompleksniIzraz(unesenoResenje);
        boolean tacno = rezultat == trazeniBroj;

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
        if (tacno && brojeviPostoje) {
            if (guest.equals(checkGuest)) {
                bodovi = bodovi + 20;
                Intent gameIntent = new Intent(MojBrojActivity.this, KoZnaZnaActivity.class);
                gameIntent.putExtra("user", "guest");
                gameIntent.putExtra("bodovi", bodovi);
                startActivity(gameIntent);
                finish();//-----------------------------------------------------------------------------------------------------------------
            } else if (WhoImI.equals("host")) {
                Toast.makeText(this, "Čestitamo! Rešenje je tačno!", Toast.LENGTH_SHORT).show();
                databaseReference.child("brojevi").child("resenjeServera").setValue(tacno);
                databaseReference.child("brojevi").child("resenjePoslano").setValue(true);
                bodovi = bodovi + 20;
                // if (WhoImI.equals("host")) {//provjeri da li je kraj druge runde
                if (zapocniIgru.equals("0")) {
                    databaseReference.child("zapocniIgru").setValue("1");
                    Intent gameIntent = new Intent(MojBrojActivity.this, MojBrojActivity.class);
                    gameIntent.putExtra("WhoImI", "klijent");
                    gameIntent.putExtra("bodovi", bodovi);
                    startActivity(gameIntent);
                    finish();
                } else if (zapocniIgru.equals("1")) {
                    databaseReference.child("zapocniIgru").setValue("2");
                }
                //}
//                else {
//                    Intent gameIntent = new Intent(MojBrojActivity.this, MojBrojActivity.class);
//                    gameIntent.putExtra("host", klijent);
//                    gameIntent.putExtra("klijent", host);
//                    gameIntent.putExtra("bodovi", bodovi);
//                    databaseReference.child("zapocniIgru").setValue("0");
//                    databaseReference.child("brojevi").child("brojRestart").setValue("0");
//                    startActivity(gameIntent);
//                    finish();
//                }
            } else if ((WhoImI.equals("klijent") && resenjeServera)) {
                Toast.makeText(this, "Čestitamo! Rešenje je tačno ali igrac cija je igra je isto pogodio!", Toast.LENGTH_SHORT).show();
                //if (klijent.equals("host")) {
                Intent gameIntent = new Intent(MojBrojActivity.this, MojBrojActivity.class);
                gameIntent.putExtra("WhoImI", "host");
                gameIntent.putExtra("bodovi", bodovi);
                startActivity(gameIntent);
                finish();

                //}
//                else {
//                    Intent gameIntent = new Intent(MojBrojActivity.this, MojBrojActivity.class);
//                    gameIntent.putExtra("host", klijent);
//                    gameIntent.putExtra("klijent", host);
//                    gameIntent.putExtra("bodovi", bodovi);
//                    databaseReference.child("zapocniIgru").setValue("0");
//                    databaseReference.child("brojevi").child("brojRestart").setValue("0");
//
//                    startActivity(gameIntent);
//                    finish();
//                }
            }
//            else if ((WhoImI.equals("klijent") && !resenjePoslano)) { =================jos nemamo klijent prvi pogodi
//                bodovi = bodovi + 20;
//                if (WhoImI.equals("klijent")) {
//                    Toast.makeText(MojBrojActivity.this, "Čestitamo! Rešenje je tačno!", Toast.LENGTH_SHORT).show();
//                    Intent gameIntent = new Intent(MojBrojActivity.this, MojBrojActivity.class);
//                    gameIntent.putExtra("WhoImI", "host");
//                    gameIntent.putExtra("bodovi", bodovi);
//                    databaseReference.child("brojevi").child("brojRestart").setValue("0");
//                    startActivity(gameIntent);
//                    finish();
//                } else {
//                    if (zapocniIgru.equals("0")) {
//                        databaseReference.child("zapocniIgru").setValue("1");
//                    } else if (zapocniIgru.equals("1")) {
//                        databaseReference.child("zapocniIgru").setValue("2");
//                    }
//                }
//            }
        } else if (!tacno) {
            int polovicniRezultat = rezultat - trazeniBroj;
            Toast.makeText(this, "Vase rešenje je za " + polovicniRezultat + " udaljeno od tacnog.", Toast.LENGTH_SHORT).show();
            if (polovicniRezultat >= -25 && polovicniRezultat <= 25) {
                bodovi = bodovi + 10;
                if (guest.equals(checkGuest)) {
                    Intent gameIntent = new Intent(MojBrojActivity.this, KoZnaZnaActivity.class);
                    gameIntent.putExtra("user", "guest");
                    gameIntent.putExtra("bodovi", bodovi);
                    startActivity(gameIntent);
                    finish();
                }
            }
        } else {
            if (guest.equals(checkGuest)) {
                Toast.makeText(this, "Neki brojevi u rešenju ne postoje među ponuđenim brojevima.", Toast.LENGTH_SHORT).show();
                Intent gameIntent = new Intent(MojBrojActivity.this, KoZnaZnaActivity.class);
                gameIntent.putExtra("user", "guest");
                gameIntent.putExtra("bodovi", bodovi);
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
                proveriResenje();
                activeTimers.remove(this);
            }
        };
        timer.start();
        activeTimers.add(timer);
    }

    private void stopAllTimers() {
        for (CountDownTimer timer : activeTimers) {
            timer.cancel();
        }
        activeTimers.clear(); // Očisti listu aktivnih tajmera
    }

    private void updateTimerText(long millisUntilFinished) {
        int seconds = (int) (millisUntilFinished / 1000);

        String time = String.format("%02d", seconds);
        timerView.setText(time);
    }

    @Override
    protected void onDestroy() {
        stopAllTimers();
        databaseReference.child("zapocniIgru").removeEventListener(zapocniIgruListener);
        databaseReference.child("brojevi").removeEventListener(brojevi);

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometerSensor != null) {
            sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometerSensor != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

}