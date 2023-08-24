package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slagalica.HomeActivity;
import com.example.slagalica.R;
import com.example.slagalica.dataBase.DBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class KoZnaZnaActivity extends AppCompatActivity {
    private DBHelper databaseHelper;

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";


    private TextView tvPitanje;
    private TextView tvOdgovor1;
    private TextView tvOdgovor2;
    private TextView tvOdgovor3;
    private TextView tvOdgovor4;

    private TextView timerView;
    private TextView bodoviView;
    private CountDownTimer timer;
    private boolean timerFinished;
    private long startTimer = 5000;

    private String checkGuest = "guest";
    private String guest;
    private int bodovi;

    private String host = "";

    private String klijent = "";

    private Boolean isFirst;

    private Boolean klijentIgra;


    private String currentQuestion;
    private String[] currentAnswers;
    private int correctAnswerIndex;
    private int questionId;

    private int currentQuestionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ko_zna_zna);

        currentQuestionId = 0;

        databaseHelper = new DBHelper(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();


        // Ubacite pitanja u bazu podataka
        databaseHelper.insertQuestion("Koja planeta je najbliža Suncu?", "Merkur", "Venera", "Mars", "Saturn", 0);
        databaseHelper.insertQuestion("Koliko je 2 + 2?", "3", "4", "5", "6", 1);
        databaseHelper.insertQuestion("Koja je najveća reka u Evropi?", "Dunav", "Volga", "Rajna", "Tisa", 1);
        databaseHelper.insertQuestion("Ko je autor knjige 'Rat i mir'?", "Lev Tolstoj", "Fjodor Dostojevski", "Anton Pavlovič Čehov", "Ivan Turgenjev", 0);
        databaseHelper.insertQuestion("Ko je napisao dramu 'Hamlet'?", "William Shakespeare", "Arthur Miller", "Antonin Artaud", "Samuel Beckett", 0);

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);
        host = getIntent().getStringExtra("host");//klijent
        klijent = getIntent().getStringExtra("klijent");//host

        timerFinished = false;

        if (guest == null) {
            guest = "";
        }

        if(!guest.equals(checkGuest)) {
            databaseReference.child("koznazna").child("amIFirst").setValue(true);
        }

        if(!guest.equals(checkGuest)) {
            databaseReference.child("koznazna").child("amIFirst").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    isFirst = dataSnapshot.getValue(Boolean.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        if(!guest.equals(checkGuest)) {
            databaseReference.child("koznazna").child("questionId").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                  //   currentQuestionId = dataSnapshot.getValue(Integer.class);
                     // ovdje bi trebalo dodat ako cemo imat vise jos pitanja
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


        timerView = findViewById(R.id.TVTimer);
        bodoviView = findViewById(R.id.TVBodovi);
        bodoviView.setText(String.valueOf(bodovi));

        tvPitanje = findViewById(R.id.TVPitanje);
        tvOdgovor1 = findViewById(R.id.TVOdgovor1);
        tvOdgovor2 = findViewById(R.id.TVOdgovor2);
        tvOdgovor3 = findViewById(R.id.TVOdgovor3);
        tvOdgovor4 = findViewById(R.id.TVOdgovor4);

        questionId = 1; // The ID of the desired question
        String[] questionDetails = databaseHelper.getQuestion(questionId);

        displayQuestion(questionDetails);
        startTimer(startTimer);

    }

    private void displayQuestion(String[] questionDetails) {
        // Prikazi pitanje

        String question = questionDetails[0];
        String answer1 = questionDetails[1];
        String answer2 = questionDetails[2];
        String answer3 = questionDetails[3];
        String answer4 = questionDetails[4];

        tvPitanje.setText(question);

        // Prikazi odgovore
        tvOdgovor1.setText(answer1);
        tvOdgovor2.setText(answer2);
        tvOdgovor3.setText(answer3);
        tvOdgovor4.setText(answer4);

        // Resetuj boje odgovora
        resetAnswerColors();

        // Dodaj click listenere na odgovore
        tvOdgovor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(0);
            }
        });

        tvOdgovor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAnswer(1);
            }
        });

        tvOdgovor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAnswer(2);
            }
        });

        tvOdgovor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAnswer(3);
            }
        });
    }

    private void checkAnswer(int selectedAnswerIndex) {
        if (selectedAnswerIndex == correctAnswerIndex) {
            if(!guest.equals(checkGuest)) {
                databaseReference.child("koznazna").child("amIFirst").setValue(!isFirst);
            }
            TextView selectedAnswerTextView = getAnswerTextView(selectedAnswerIndex);
            selectedAnswerTextView.setBackgroundColor(Color.GREEN);
            if(!guest.equals(checkGuest)){
                if (isFirst ) {
                    bodovi = bodovi + 10;
                }
                else {
                    Toast.makeText(this, "Nazalost, protivnik vas je preduhitrio.", Toast.LENGTH_SHORT).show();
                }
            } else {
                bodovi = bodovi + 10;
            }

            bodoviView.setText(String.valueOf(bodovi));


        } else if(selectedAnswerIndex == -1){
            TextView correctAnswerTextView = getAnswerTextView(correctAnswerIndex);
            correctAnswerTextView.setBackgroundColor(Color.GREEN);
        }
        else {
            TextView correctAnswerTextView = getAnswerTextView(correctAnswerIndex);
            TextView selectedAnswerTextView = getAnswerTextView(selectedAnswerIndex);
            bodovi = bodovi - 5;
            bodoviView.setText(String.valueOf(bodovi));
            correctAnswerTextView.setBackgroundColor(Color.GREEN);
            selectedAnswerTextView.setBackgroundColor(Color.RED);
        }

        if (!timerFinished) {
            timer.cancel();
            timerFinished = true;
        }

        disableAnswerClicks();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveToNextQuestion();
                databaseReference.child("koznazna").child("amIFirst").setValue(true);

            }
        }, 2000);
    }

    private void resetAnswerColors() {
        tvOdgovor1.setBackgroundColor(Color.TRANSPARENT);
        tvOdgovor2.setBackgroundColor(Color.TRANSPARENT);
        tvOdgovor3.setBackgroundColor(Color.TRANSPARENT);
        tvOdgovor4.setBackgroundColor(Color.TRANSPARENT);
    }

    private void disableAnswerClicks() {
        tvOdgovor1.setOnClickListener(null);
        tvOdgovor2.setOnClickListener(null);
        tvOdgovor3.setOnClickListener(null);
        tvOdgovor4.setOnClickListener(null);
    }

    private TextView getAnswerTextView(int answerIndex) {
        switch (answerIndex) {
            case 0:
                return tvOdgovor1;
            case 1:
                return tvOdgovor2;
            case 2:
                return tvOdgovor3;
            case 3:
                return tvOdgovor4;
            default:
                return null;
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
                checkAnswer(-1);
            }
        };

        timer.start();
        timerFinished = false;
    }


    private void updateTimerText(long millisUntilFinished) {
        int seconds = (int) (millisUntilFinished / 1000);

        String time = String.format("%02d", seconds);
        timerView.setText(time);
    }

    private void restartTimer() {
        if (!timerFinished) {
            timer.cancel();
            timerFinished = true;
        }

        timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timerView.setText("00");

                checkAnswer(-1);
            }
        };

        timer.start();
        timerFinished = false;
    }

    private void moveToNextQuestion() {
        String[] nextQuestion = databaseHelper.getQuestion(questionId + 1);
        questionId += 1;
        if (questionId > 5) {
            timer.cancel();
            if(host == null ){
            Intent intent = new Intent(KoZnaZnaActivity.this, KorakPoKorakActivity.class);
            intent.putExtra("user", "guest");
            intent.putExtra("bodovi", bodovi);
            startActivity(intent);
            finish();
            } else {
                Intent intent = new Intent(KoZnaZnaActivity.this, ResultActivity.class);
                intent.putExtra("bodovi", bodovi);
                startActivity(intent);
                finish();
            }
        } else if (nextQuestion != null) {
            currentQuestion = nextQuestion[0];
            currentAnswers = new String[]{
                    nextQuestion[1],
                    nextQuestion[2],
                    nextQuestion[3],
                    nextQuestion[4]
            };
            correctAnswerIndex = Integer.parseInt(nextQuestion[5]);

            // Prikazivanje sledećeg pitanja
            displayQuestion(nextQuestion);
            restartTimer();
        }
    }
}