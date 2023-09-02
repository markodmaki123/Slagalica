package com.example.slagalica.games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Random;

public class KoZnaZnaActivity extends AppCompatActivity {
    private DBHelper databaseHelper;

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";


    private TextView tvPitanje;
    private TextView tvOdgovor1;
    private TextView tvOdgovor2;
    private TextView tvOdgovor3;
    private TextView tvOdgovor4;
    private Button btnKreni;
    private TextView tvProtinik;

    private TextView timerView;
    private TextView bodoviView;
    private CountDownTimer timer;
    private boolean timerFinished;
    private long startTimer = 5000;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private String checkGuest = "guest";
    private String guest;
    private String WhoImI = "";
    private int bodovi;

    String[] questionDetails;
    ArrayList<Integer> questionsGenerated = new ArrayList<>();
    private int number1Generated = 0;
    private int number2Generated = 0;
    private int number3Generated = 0;
    private int number4Generated = 0;
    private int number5Generated = 0;
    private String timerStart = "";

    private Boolean isFirst;


    private String currentQuestion;
    private String[] currentAnswers;
    private int correctAnswerIndex;
    private int questionId;

    private int currentQuestionId;
    private long remainingTime;

    private ValueEventListener timerListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            timerStart = dataSnapshot.getValue(String.class);
            if(timerStart.equals("1")) {
                startTimer(startTimer);
                questionsGenerated.add(number1Generated);
                questionsGenerated.add(number2Generated);
                questionsGenerated.add(number3Generated);
                questionsGenerated.add(number4Generated);
                questionsGenerated.add(number5Generated);
                questionDetails = databaseHelper.getQuestion(questionsGenerated.get(0));
                correctAnswerIndex = Integer.parseInt(questionDetails[5]);
                tvPitanje.setVisibility(View.VISIBLE);
                tvOdgovor1.setVisibility(View.VISIBLE);
                tvOdgovor2.setVisibility(View.VISIBLE);
                tvOdgovor3.setVisibility(View.VISIBLE);
                tvOdgovor4.setVisibility(View.VISIBLE);
                displayQuestion(questionDetails);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ko_zna_zna);

        currentQuestionId = 0;

        btnKreni = findViewById(R.id.btnStart);

        databaseHelper = new DBHelper(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        // Ubacite pitanja u bazu podataka
        databaseHelper.insertQuestion("Koja planeta je najbliža Suncu?", "Merkur", "Venera", "Mars", "Saturn", 0);
        databaseHelper.insertQuestion("Koliko je 2 + 2?", "3", "4", "5", "6", 1);
        databaseHelper.insertQuestion("Koja je najveća reka u Evropi?", "Dunav", "Volga", "Rajna", "Tisa", 1);
        databaseHelper.insertQuestion("Ko je autor knjige 'Rat i mir'?", "Lev Tolstoj", "Fjodor Dostojevski", "Anton Pavlovič Čehov", "Ivan Turgenjev", 0);
        databaseHelper.insertQuestion("Ko je napisao dramu 'Hamlet'?", "William Shakespeare", "Arthur Miller", "Antonin Artaud", "Samuel Beckett", 0);
        databaseHelper.insertQuestion("Koji od sljedecih timova nisu u Formuli 1?", "Ferrari", "Mercedes", "Citroen", "RedBull", 2);
        databaseHelper.insertQuestion("Ko je osvojio Ligu sampiona 2022/2023 u fudbalu?", "Real Madrid", "PSG", "Bayern Munich", "Manchester City", 3);
        databaseHelper.insertQuestion("Ko je osvojio Svjetsko prvenstvo u fudbalu 2022. godine?", "Argentina", "Holandija", "Engleska", "Francuska", 0);
        databaseHelper.insertQuestion("Koliko okeana ima na svijetu?", "6", "4", "7", "5", 3);
        databaseHelper.insertQuestion("Ko je osvojio NBA sampiona u sezoni 2022/2023?", "Philadelphia 76er's", "Los Angeles Lakers", "Denver Nuggets", "Golden State Warriors", 2);

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);
        WhoImI = getIntent().getStringExtra("WhoImI");

        timerFinished = false;

        if (guest == null) {
            guest = "";
        }
        if (WhoImI == null) {
            WhoImI = "";
        }

        if (!guest.equals(checkGuest)) {
            databaseReference.child("koznazna").child("amIFirst").setValue(true);
        }

        if (!guest.equals(checkGuest)) {
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


        if (WhoImI.equals("klijent")) {
            databaseReference.child("koznazna").child("Timer").addValueEventListener(timerListener);
            btnKreni.setVisibility(View.INVISIBLE);
            DatabaseReference koznaznaRef = databaseReference.child("koznazna");

            koznaznaRef.child("question1Id").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer value = snapshot.getValue(Integer.class);
                    if (value != null) {
                        number1Generated = value;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });

            koznaznaRef.child("question2Id").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer value = snapshot.getValue(Integer.class);
                    if (value != null) {
                        number2Generated = value;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });

            // Repeat the above code for question3id and question4id
            koznaznaRef.child("question3Id").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer value = snapshot.getValue(Integer.class);
                    if (value != null) {
                        number3Generated = value;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });

            koznaznaRef.child("question4Id").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer value = snapshot.getValue(Integer.class);
                    if (value != null) {
                        number4Generated = value;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });

            koznaznaRef.child("question5Id").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer value = snapshot.getValue(Integer.class);
                    if (value != null) {
                        number5Generated = value;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });
        }



        timerView = findViewById(R.id.TVTimer);
        bodoviView = findViewById(R.id.TVBodovi);
        bodoviView.setText(String.valueOf(bodovi));
        tvProtinik = findViewById(R.id.TVProtivnik);

        String protivnik = sharedPreferences.getString("mojProtivnik", "");

        tvProtinik.setText("VS " + protivnik);

        tvPitanje = findViewById(R.id.TVPitanje);
        tvOdgovor1 = findViewById(R.id.TVOdgovor1);
        tvOdgovor2 = findViewById(R.id.TVOdgovor2);
        tvOdgovor3 = findViewById(R.id.TVOdgovor3);
        tvOdgovor4 = findViewById(R.id.TVOdgovor4);

        if (!guest.equals(checkGuest) && WhoImI.equals("host")) {
            tvPitanje.setVisibility(View.INVISIBLE);
            tvOdgovor1.setVisibility(View.INVISIBLE);
            tvOdgovor2.setVisibility(View.INVISIBLE);
            tvOdgovor3.setVisibility(View.INVISIBLE);
            tvOdgovor4.setVisibility(View.INVISIBLE);
            btnKreni.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    questionId = 0;
                    questionsGenerated = generateQuestions();
                    databaseReference.child("koznazna").child("question1Id").setValue(questionsGenerated.get(0));
                    databaseReference.child("koznazna").child("question2Id").setValue(questionsGenerated.get(1));
                    databaseReference.child("koznazna").child("question3Id").setValue(questionsGenerated.get(2));
                    databaseReference.child("koznazna").child("question4Id").setValue(questionsGenerated.get(3));
                    databaseReference.child("koznazna").child("question5Id").setValue(questionsGenerated.get(4));
                    questionDetails = databaseHelper.getQuestion(questionsGenerated.get(0));
                    correctAnswerIndex = Integer.parseInt(questionDetails[5]);
                    tvPitanje.setVisibility(View.VISIBLE);
                    tvOdgovor1.setVisibility(View.VISIBLE);
                    tvOdgovor2.setVisibility(View.VISIBLE);
                    tvOdgovor3.setVisibility(View.VISIBLE);
                    tvOdgovor4.setVisibility(View.VISIBLE);
                    displayQuestion(questionDetails);
                    startTimer(startTimer);
                    databaseReference.child("koznazna").child("Timer").setValue("1");
                    btnKreni.setVisibility(View.INVISIBLE);
                }
            });
        } else if (guest.equals(checkGuest)) {
            btnKreni.setVisibility(View.INVISIBLE);
            questionId = 0;
            questionsGenerated = generateQuestions();
            questionDetails = databaseHelper.getQuestion(questionsGenerated.get(0));
            displayQuestion(questionDetails);
            correctAnswerIndex = Integer.parseInt(questionDetails[5]);
            startTimer(startTimer);
        } else if (WhoImI.equals("klijent")) {
            tvPitanje.setVisibility(View.INVISIBLE);
            tvOdgovor1.setVisibility(View.INVISIBLE);
            tvOdgovor2.setVisibility(View.INVISIBLE);
            tvOdgovor3.setVisibility(View.INVISIBLE);
            tvOdgovor4.setVisibility(View.INVISIBLE);
            questionId = 0;
        }
    }

    private ArrayList<Integer> generateQuestions() {
        int min = 1;
        int max = 10;
        int count = 5;

        ArrayList<Integer> numbers = new ArrayList<>();
        Random rand = new Random();

        while (numbers.size() < count) {
            int randomNumber = rand.nextInt(max - min + 1) + min;

            if (!numbers.contains(randomNumber)) {
                numbers.add(randomNumber);
            }
        }
        return numbers;
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
            if (!guest.equals(checkGuest)) {
                databaseReference.child("koznazna").child("amIFirst").setValue(!isFirst);
            }
            TextView selectedAnswerTextView = getAnswerTextView(selectedAnswerIndex);
            selectedAnswerTextView.setBackgroundColor(Color.GREEN);
            if (!guest.equals(checkGuest)) {
                if (isFirst) {
                    bodovi = bodovi + 10;
                } else {
                    Toast.makeText(this, "Nazalost, protivnik vas je preduhitrio.", Toast.LENGTH_SHORT).show();
                }
            } else {
                bodovi = bodovi + 10;
            }

            bodoviView.setText(String.valueOf(bodovi));


        } else if (selectedAnswerIndex == -1) {
            TextView correctAnswerTextView = getAnswerTextView(correctAnswerIndex);
            correctAnswerTextView.setBackgroundColor(Color.GREEN);
        } else {
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
        }, remainingTime);
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
        remainingTime = time;

        timer = new CountDownTimer(time, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
                remainingTime = millisUntilFinished;
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

        timer = new CountDownTimer(6000, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
                remainingTime = millisUntilFinished;
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
        if (questionId == 4) {
            timer.cancel();
            if (guest.equals(checkGuest)) {
                Intent intent = new Intent(KoZnaZnaActivity.this, KorakPoKorakActivity.class);
                intent.putExtra("user", "guest");
                intent.putExtra("bodovi", bodovi);
                startActivity(intent);
                finish();
            } else if (WhoImI.equals("host") || WhoImI.equals("klijent")) {
                Intent intent = new Intent(KoZnaZnaActivity.this, KorakPoKorakActivity.class);
                intent.putExtra("WhoImI", WhoImI);
                intent.putExtra("bodovi", bodovi);
                startActivity(intent);
                finish();
            }
        } else {
            questionId += 1;
            String[] nextQuestion = databaseHelper.getQuestion(questionsGenerated.get(questionId));
            if (nextQuestion != null) {
                currentQuestion = nextQuestion[0];
                currentAnswers = new String[]{
                        nextQuestion[1],
                        nextQuestion[2],
                        nextQuestion[3],
                        nextQuestion[4]
                };
                correctAnswerIndex = Integer.parseInt(nextQuestion[5]);
                displayQuestion(nextQuestion);
                restartTimer();
            }
        }
    }
}