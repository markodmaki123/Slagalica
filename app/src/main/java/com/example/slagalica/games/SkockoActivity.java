package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slagalica.HomeActivity;
import com.example.slagalica.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkockoActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";

    private List<String> userCombination = new ArrayList<>();
    private int attemptCount = 0;
    private List<String> combination = new ArrayList<>();
    private List<CountDownTimer> activeTimers = new ArrayList<>();

    private ImageView imgSkocko;
    private ImageView imgTref;
    private ImageView imgPik;
    private ImageView imgSrce;
    private ImageView imgKaro;
    private ImageView imgZvijezda;
    private TextView tvProtinik;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private ImageView konacno1;
    private ImageView konacno2;
    private ImageView konacno3;
    private ImageView konacno4;


    private TextView timerView;
    private TextView bodoviView;
    private CountDownTimer timer;
    private long startTimer = 45000;

    private String checkGuest = "guest";
    private String guest;
    private int bodovi;

    private int bodovi1;
    private int bodovi2;

    private String WhoImI = "";
    private Integer potez = 0;
    String zapocniIgru = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skocko);

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String[] symbols = {"skocko", "tref", "pik", "srce", "karo", "zvijezda"};

        imgSkocko = findViewById(R.id.skocko);
        imgTref = findViewById(R.id.tref);
        imgPik = findViewById(R.id.pik);
        imgSrce = findViewById(R.id.srce);
        imgKaro = findViewById(R.id.karo);
        imgZvijezda = findViewById(R.id.zvijezda);

        tvProtinik = findViewById(R.id.TVProtivnik);
        konacno1 = findViewById(R.id.IVkonacno1);
        konacno2 = findViewById(R.id.IVkonacno2);
        konacno3 = findViewById(R.id.IVkonacno3);
        konacno4 = findViewById(R.id.IVkonacno4);

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);
        WhoImI = getIntent().getStringExtra("WhoImI");

        String protivnik = sharedPreferences.getString("mojProtivnik", "");

        tvProtinik.setText("VS " + protivnik);

        timerView = findViewById(R.id.TVTimer);
        bodoviView = findViewById(R.id.TVBodovi);

        bodoviView.setText(String.valueOf(bodovi));

        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (guest == null) {
            guest = "";
        }
        if (WhoImI == null) {
            WhoImI = "";
        }
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int randomIndex = random.nextInt(symbols.length);
            String symbol = symbols[randomIndex];
            combination.add(symbol);
        }

        int drawableId1 = getResources().getIdentifier(combination.get(0), "drawable", getPackageName());
        konacno1.setImageResource(drawableId1);
        int drawableId2 = getResources().getIdentifier(combination.get(1), "drawable", getPackageName());
        konacno2.setImageResource(drawableId2);
        int drawableId3 = getResources().getIdentifier(combination.get(2), "drawable", getPackageName());
        konacno3.setImageResource(drawableId3);
        int drawableId4 = getResources().getIdentifier(combination.get(3), "drawable", getPackageName());
        konacno4.setImageResource(drawableId4);

        konacno1.setVisibility(View.INVISIBLE);
        konacno2.setVisibility(View.INVISIBLE);
        konacno3.setVisibility(View.INVISIBLE);
        konacno4.setVisibility(View.INVISIBLE);


        databaseReference.child("Skocko").child("BrojPokusaja").setValue(0);
        databaseReference.child("Skocko").child("combination").child("simbol1").setValue(combination.get(0));
        databaseReference.child("Skocko").child("combination").child("simbol2").setValue(combination.get(1));
        databaseReference.child("Skocko").child("combination").child("simbol3").setValue(combination.get(2));
        databaseReference.child("Skocko").child("combination").child("simbol4").setValue(combination.get(3));

        imgSkocko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSymbolClicked(v);
            }
        });

        imgTref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSymbolClicked(v);
            }
        });

        imgPik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSymbolClicked(v);
            }
        });

        imgSrce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSymbolClicked(v);
            }
        });
        imgKaro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSymbolClicked(v);
            }
        });

        imgZvijezda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSymbolClicked(v);
            }
        });

        startTimer(startTimer);



        databaseReference.child("zapocniIgru").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                zapocniIgru = value;
                if (value.equals("2")) {
                    Intent intent = new Intent(SkockoActivity.this, ResultActivity.class);
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
                    Intent intent = new Intent(SkockoActivity.this, SkockoActivity.class);
                    intent.putExtra("bodovi", bodovi);
                    databaseReference.child("zapocniIgru").setValue("1.5");
                    if (WhoImI.equals("host")) {
                        intent.putExtra("WhoImI", "klijent");
                    } else if (WhoImI.equals("klijent")) {
                        intent.putExtra("WhoImI", "host");
                    }
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (!guest.equals(checkGuest)) {
            databaseReference.child("Skocko").child("BrojPokusaja").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    potez = dataSnapshot.getValue(Integer.class);
                    int valueInt = potez.intValue();
                    if (valueInt == 6 && WhoImI.equals("host")) {
                        imgSkocko.setEnabled(false);
                        imgTref.setEnabled(false);
                        imgPik.setEnabled(false);
                        imgSrce.setEnabled(false);
                        imgKaro.setEnabled(false);
                        imgZvijezda.setEnabled(false);
                    } else if (valueInt != 7 && WhoImI.equals("klijent")) {
                        if (potez != 0) {
                            imgSkocko.setEnabled(false);
                            imgTref.setEnabled(false);
                            imgPik.setEnabled(false);
                            imgSrce.setEnabled(false);
                            imgKaro.setEnabled(false);
                            imgZvijezda.setEnabled(false);
                            databaseReference.child("Skocko").addValueEventListener(updateKlijentHints);
                            databaseReference.child("Skocko").child("userCombination").addValueEventListener(updateKlijentCombinationUI);
                        }
                    }
                     if (valueInt == 6 && WhoImI.equals("klijent")) {
                        imgSkocko.setEnabled(true);
                        imgTref.setEnabled(true);
                        imgPik.setEnabled(true);
                        imgSrce.setEnabled(true);
                        imgKaro.setEnabled(true);
                        imgZvijezda.setEnabled(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


    }

    public void onSymbolClicked(View view) {
        ImageView symbolImageView = (ImageView) view;
        String symbol = getSymbolFromImageView(symbolImageView);

        if (symbol != null) {
            userCombination.add(symbol);
            if (WhoImI.equals("host") || guest.equals(checkGuest)) {
                updateUserCombinationUI();
            } else if (WhoImI.equals("klijent")) {
                updateUserCombinationUIKlijent();
            }
        }
        if (userCombination.size() == 4) {
            /////UBACUJEM U BAZU POKUSAJ HOSTA
            databaseReference.child("Skocko").child("userCombination").child("simbol1").setValue(userCombination.get(0));
            databaseReference.child("Skocko").child("userCombination").child("simbol2").setValue(userCombination.get(1));
            databaseReference.child("Skocko").child("userCombination").child("simbol3").setValue(userCombination.get(2));
            databaseReference.child("Skocko").child("userCombination").child("simbol4").setValue(userCombination.get(3));
            if (guest.equals(checkGuest)) {
                attemptCount++;
                evaluateCombination();
                clearUserCombination();
            } else if (WhoImI.equals("klijent")) {
                attemptCount++;
                potez++;
                evaluateCombination();
                clearUserCombination();
                databaseReference.child("Skocko").child("BrojPokusaja").setValue(potez);
            } else if (WhoImI.equals("host")) {
                attemptCount++;
                potez++;
                evaluateCombination();
                clearUserCombination();
                databaseReference.child("Skocko").child("BrojPokusaja").setValue(potez);
            }

        }
    }

    private String getSymbolFromImageView(ImageView imageView) {
        switch (imageView.getId()) {
            case R.id.skocko:
                return "skocko";
            case R.id.tref:
                return "tref";
            case R.id.pik:
                return "pik";
            case R.id.srce:
                return "srce";
            case R.id.karo:
                return "karo";
            case R.id.zvijezda:
                return "zvijezda";
            default:
                return null;
        }
    }

    private void updateUserCombinationUI() {
        int index = userCombination.size() - 1;
        int imageViewId = getResources().getIdentifier("IVpokusaj" + (attemptCount + 1) + (index + 1), "id", getPackageName());
        ImageView imageView = findViewById(imageViewId);

        String symbol = userCombination.get(index);
        int drawableId = getResources().getIdentifier(symbol, "drawable", getPackageName());
        imageView.setImageResource(drawableId);
    }

    private void updateUserCombinationUIKlijent() {
        int index = userCombination.size() - 1;
        int imageViewId = getResources().getIdentifier("IVpokusaj7" + (index + 1), "id", getPackageName());
        ImageView imageView = findViewById(imageViewId);

        String symbol = userCombination.get(index);
        int drawableId = getResources().getIdentifier(symbol, "drawable", getPackageName());
        imageView.setImageResource(drawableId);
    }

    private void evaluateCombination() {
        int correctSymbols = 0;
        int misplacedSymbols = 0;

        List<String> combinationCopy = new ArrayList<>(combination);

        // Proverite koliko tačnih simbola se nalazi na pravom mestu
        for (int i = 0; i < userCombination.size(); i++) {
            String userSymbol = userCombination.get(i);
            String generatedSymbol = combinationCopy.get(i);
            if (userSymbol.equals(generatedSymbol)) {
                correctSymbols++;
                combinationCopy.set(i, null);
                userCombination.set(i, null);
            }
        }

        // Proverite koliko tačnih simbola se nalazi na pogrešnom mestu
        // i da nije višestruko prisutan u generisanoj kombinaciji
        for (int i = 0; i < userCombination.size(); i++) {
            if (userCombination.get(i) != null) {
                if (combinationCopy.contains(userCombination.get(i))) {
                    misplacedSymbols++;
                    combinationCopy.set(combinationCopy.indexOf(userCombination.get(i)), null);
                    userCombination.set(i, null);
                }

            }
        }

        if ((WhoImI.equals("host")) || (guest.equals(checkGuest))) {
            int textViewId = getResources().getIdentifier("TVhint" + attemptCount, "id", getPackageName());
            TextView textView = findViewById(textViewId);
            String resultText = "T: " + correctSymbols + ", NNSM: " + misplacedSymbols;
            textView.setText(resultText);
            databaseReference.child("Skocko").child("hints").setValue(resultText);
        }else if(WhoImI.equals("klijent")){
            int textViewId = getResources().getIdentifier("TVhint7" , "id", getPackageName());
            TextView textView = findViewById(textViewId);
            String resultText = "T: " + correctSymbols + ", NNSM: " + misplacedSymbols;
            textView.setText(resultText);
        }


        // Proverite da li je korisnik pogodio kombinaciju ili dostigao maksimalan broj pokušaja
        if (correctSymbols == 4) {
            if (attemptCount == 1 || attemptCount == 2) {
                bodovi = bodovi + 20;
            } else if (attemptCount == 3 || attemptCount == 4) {
                bodovi = bodovi + 15;
            } else if (attemptCount == 5 || attemptCount == 6) {
                bodovi = bodovi + 10;
            }
            bodoviView.setText(String.valueOf(bodovi));
            stopAllTimers();
            Toast.makeText(this, "Bravo pogodiliste tacnu kobinaciju!", Toast.LENGTH_SHORT).show();
            imgSkocko.setEnabled(false);
            imgTref.setEnabled(false);
            imgPik.setEnabled(false);
            imgSrce.setEnabled(false);
            imgKaro.setEnabled(false);
            imgZvijezda.setEnabled(false);
            konacno1.setVisibility(View.VISIBLE);
            konacno2.setVisibility(View.VISIBLE);
            konacno3.setVisibility(View.VISIBLE);
            konacno4.setVisibility(View.VISIBLE);
            if (guest.equals(checkGuest)) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SkockoActivity.this, ResultActivity.class);
                        intent.putExtra("user", "guest");
                        intent.putExtra("bodovi", bodovi);
                        startActivity(intent);
                        finish();
                    }
                }, 3000);
            } else if (WhoImI.equals("host")) {
                stopAllTimers();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (zapocniIgru.equals("0")) {
                            databaseReference.child("zapocniIgru").setValue("1");
                            databaseReference.child("Skocko").child("BrojPokusaja").setValue(0);
                        } else if (zapocniIgru.equals("1.5")) {
                            databaseReference.child("zapocniIgru").setValue("2");
                        }
                    }
                }, 2000);
            } else if (WhoImI.equals("klijent")) {
                stopAllTimers();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (zapocniIgru.equals("0")) {
                            databaseReference.child("zapocniIgru").setValue("1");
                            databaseReference.child("Skocko").child("BrojPokusaja").setValue(0);
                        } else if (zapocniIgru.equals("1.5")) {
                            databaseReference.child("zapocniIgru").setValue("2");
                        }
                    }
                }, 2000);
            }
        } else if ((attemptCount == 6) && (guest.equals(checkGuest))) {
            tacnoResenje(combination.get(0), combination.get(1), combination.get(2), combination.get(3));
            imgSkocko.setEnabled(false);
            imgTref.setEnabled(false);
            imgPik.setEnabled(false);
            imgSrce.setEnabled(false);
            imgKaro.setEnabled(false);
            imgZvijezda.setEnabled(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SkockoActivity.this, ResultActivity.class);
                    intent.putExtra("user", "guest");
                    intent.putExtra("bodovi", bodovi);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        } else if ((potez == 7) && WhoImI.equals("klijent")) {
            tacnoResenje(combination.get(0), combination.get(1), combination.get(2), combination.get(3));
            imgSkocko.setEnabled(false);
            imgTref.setEnabled(false);
            imgPik.setEnabled(false);
            imgSrce.setEnabled(false);
            imgKaro.setEnabled(false);
            imgZvijezda.setEnabled(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (zapocniIgru.equals("0")) {
                        databaseReference.child("zapocniIgru").setValue("1");
                        databaseReference.child("Skocko").child("BrojPokusaja").setValue(0);
                    } else if (zapocniIgru.equals("1.5")) {
                        databaseReference.child("zapocniIgru").setValue("2");
                    }
                }
            }, 2000);
        }
    }

    private void tacnoResenje(String prva, String druga, String treca, String cetvrta) {
        Toast.makeText(this, "Tacna kombinacija je: " + prva + " " + druga + " " + treca + " " + cetvrta, Toast.LENGTH_SHORT).show();
        konacno1.setVisibility(View.VISIBLE);
        konacno2.setVisibility(View.VISIBLE);
        konacno3.setVisibility(View.VISIBLE);
        konacno4.setVisibility(View.VISIBLE);
    }

    private void clearUserCombination() {
        userCombination.clear();
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
                Toast.makeText(SkockoActivity.this, "Vrijeme je isteklo!", Toast.LENGTH_SHORT).show();
                activeTimers.remove(this);
                if(!guest.equals(checkGuest)) {
                    if (zapocniIgru.equals("0")) {
                        databaseReference.child("zapocniIgru").setValue("1");
                        databaseReference.child("Skocko").child("BrojPokusaja").setValue(0);
                    } else if (zapocniIgru.equals("1.5")) {
                        databaseReference.child("zapocniIgru").setValue("2");
                    }
                }else{
                    Intent intent = new Intent(SkockoActivity.this, AsocijacijeActivity.class);
                    intent.putExtra("user", "guest");
                    intent.putExtra("bodovi", bodovi);
                    startActivity(intent);
                    finish();
                }
            }
        };

        timer.start();
        activeTimers.add(timer);
    }

    private ValueEventListener updateKlijentCombinationUI = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String simbol1 = dataSnapshot.child("simbol1").getValue(String.class);
            String simbol2 = dataSnapshot.child("simbol2").getValue(String.class);
            String simbol3 = dataSnapshot.child("simbol3").getValue(String.class);
            String simbol4 = dataSnapshot.child("simbol4").getValue(String.class);

            ArrayList<String> hostCombination = new ArrayList<>();
            hostCombination.add(simbol1);
            hostCombination.add(simbol2);
            hostCombination.add(simbol3);
            hostCombination.add(simbol4);
            for (int i = 0; i < 4; i++) {
                int imageViewId = getResources().getIdentifier("IVpokusaj" + (potez) + (i + 1), "id", getPackageName());
                ImageView imageView = findViewById(imageViewId);
                String symbol = hostCombination.get(i);
                int drawableId = getResources().getIdentifier(symbol, "drawable", getPackageName());
                imageView.setImageResource(drawableId);
            }
            databaseReference.child("Skocko").child("userCombination").removeEventListener(updateKlijentCombinationUI);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private ValueEventListener updateKlijentHints = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String hint = dataSnapshot.child("hints").getValue(String.class);
            int textViewId = getResources().getIdentifier("TVhint" + (potez), "id", getPackageName());
            TextView textView = findViewById(textViewId);
            textView.setText(hint);
            databaseReference.child("Skocko").removeEventListener(updateKlijentHints);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };


    private void updateTimerText(long millisUntilFinished) {
        int seconds = (int) (millisUntilFinished / 1000);

        String time = String.format("%02d", seconds);
        timerView.setText(time);
    }

    private void stopAllTimers() {
        for (CountDownTimer timer : activeTimers) {
            timer.cancel();
        }
        activeTimers.clear(); // Očisti listu aktivnih tajmera
    }

    @Override
    protected void onDestroy() {
        stopAllTimers();
        super.onDestroy();
    }
}