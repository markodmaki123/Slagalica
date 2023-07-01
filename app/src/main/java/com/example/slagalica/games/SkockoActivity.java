package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkockoActivity extends AppCompatActivity {

    private List<String> userCombination = new ArrayList<>();
    private int attemptCount = 0;
    private List<String> combination = new ArrayList<>();

    private ImageView imgSkocko;
    private ImageView imgTref;
    private ImageView imgPik;
    private ImageView imgSrce;
    private ImageView imgKaro;
    private ImageView imgZvijezda;

    private TextView timerView;
    private TextView bodoviView;
    private CountDownTimer timer;
    private boolean timerActive = false;
    private long startTimer = 30000;

    private String checkGuest = "guest";
    private String guest;
    private int bodovi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skocko);

        String[] symbols = {"skocko", "tref", "pik", "srce", "karo", "zvijezda"};

        imgSkocko = findViewById(R.id.skocko);
        imgTref = findViewById(R.id.tref);
        imgPik = findViewById(R.id.pik);
        imgSrce = findViewById(R.id.srce);
        imgKaro = findViewById(R.id.karo);
        imgZvijezda = findViewById(R.id.zvijezda);

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);

        timerView = findViewById(R.id.TVTimer);
        bodoviView = findViewById(R.id.TVBodovi);

        bodoviView.setText(String.valueOf(bodovi));

        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int randomIndex = random.nextInt(symbols.length);
            String symbol = symbols[randomIndex];
            combination.add(symbol);
        }

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
    }

    public void onSymbolClicked(View view) {
        ImageView symbolImageView = (ImageView) view;
        String symbol = getSymbolFromImageView(symbolImageView);

        if (symbol != null) {
            userCombination.add(symbol);
            updateUserCombinationUI();
        }

        if (userCombination.size() == 4) {
            attemptCount++;
            evaluateCombination();
            clearUserCombination();
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

        int textViewId = getResources().getIdentifier("TVhint" + attemptCount, "id", getPackageName());

        TextView textView = findViewById(textViewId);

        String resultText = "T: " + correctSymbols + ", NNSM: " + misplacedSymbols;
        textView.setText(resultText);
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
            timer.cancel();
            Toast.makeText(this, "Bravo pogodiliste tacnu kobinaciju!", Toast.LENGTH_SHORT).show();
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
        } else if (attemptCount == 6) {
            tacnoResenje(combination.get(0), combination.get(1), combination.get(2), combination.get(3));
            timer.cancel();
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
                    startActivity(intent);
                    intent.putExtra("user", "guest");
                    intent.putExtra("bodovi", bodovi);
                    finish();
                }
            }, 3000);
        }
    }

    private void tacnoResenje(String prva, String druga, String treca, String cetvrta) {
        Toast.makeText(this, "Tacna kombinacija je: " + prva + " " + druga + " " + treca + " " + cetvrta, Toast.LENGTH_SHORT).show();
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
                timerActive = false;
                Toast.makeText(SkockoActivity.this, "Vrijeme je isteklo!", Toast.LENGTH_SHORT).show();
                timer.cancel();
                Intent intent = new Intent(SkockoActivity.this, ResultActivity.class);
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