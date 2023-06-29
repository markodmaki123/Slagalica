package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slagalica.HomeActivity;
import com.example.slagalica.R;
import com.example.slagalica.dataBase.DBHelper;

public class AsocijacijeActivity extends AppCompatActivity {

    private DBHelper databaseHelper;

    private TextView tvKolona11;
    private TextView tvKolona12;
    private TextView tvKolona13;
    private TextView tvKolona14;
    private TextView tvKolona21;
    private TextView tvKolona22;
    private TextView tvKolona23;
    private TextView tvKolona24;
    private TextView tvKolona31;
    private TextView tvKolona32;
    private TextView tvKolona33;
    private TextView tvKolona34;
    private TextView tvKolona41;
    private TextView tvKolona42;
    private TextView tvKolona43;
    private TextView tvKolona44;
    private EditText etKolonaOdgovor1;
    private EditText etKolonaOdgovor2;
    private EditText etKolonaOdgovor3;
    private EditText etKolonaOdgovor4;
    private EditText etOdgovorKonacno;
    private Button btnAsocijacije;

    private String correctAnswer1;
    private String correctAnswer2;
    private String correctAnswer3;
    private String correctAnswer4;

    private String correctAnswer;

    private int counterLifes;
    private String[] associationDetails;

    private int associoationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asocijacije);

        databaseHelper = new DBHelper(this);

        databaseHelper.insertAssociations("SALATA", "JUG",
                "KIVI", "MANGO",
                "VOCE", "INDUSTRIJA",
                "PRERADJIVAC", "KORIJEN",
                "GRADJA", "DRVO",
                "BEOGRAD", "NOVI SAD",
                "NIS", "ZBOGOM",
                "SRBIJA", "MALI",
                "UNIJA", "KUP NACIJA",
                "KRALJICA", "AFRIKA",
                "SLJIVA"
        );

        tvKolona11 = findViewById(R.id.kolona11);
        String kolona11 = "Kolona 11";
        tvKolona11.setText(kolona11);
        tvKolona12 = findViewById(R.id.kolona12);
        String kolona12 = "Kolona 12";
        tvKolona12.setText(kolona12);
        tvKolona13 = findViewById(R.id.kolona13);
        String kolona13 = "Kolona 13";
        tvKolona13.setText(kolona13);
        tvKolona14 = findViewById(R.id.kolona14);
        String kolona14 = "Kolona 14";
        tvKolona14.setText(kolona14);
        etKolonaOdgovor1 = findViewById(R.id.ET1);

        tvKolona21 = findViewById(R.id.kolona21);
        String kolona21 = "Kolona 21";
        tvKolona21.setText(kolona11);
        tvKolona22 = findViewById(R.id.kolona22);
        String kolona22 = "Kolona 22";
        tvKolona22.setText(kolona22);
        tvKolona23 = findViewById(R.id.kolona23);
        String kolona23 = "Kolona 23";
        tvKolona23.setText(kolona23);
        tvKolona24 = findViewById(R.id.kolona24);
        String kolona24 = "Kolona 24";
        tvKolona24.setText(kolona24);
        etKolonaOdgovor2 = findViewById(R.id.ET2);

        tvKolona31 = findViewById(R.id.kolona31);
        String kolona31 = "Kolona 31";
        tvKolona31.setText(kolona31);
        tvKolona32 = findViewById(R.id.kolona32);
        String kolona32 = "Kolona 32";
        tvKolona32.setText(kolona32);
        tvKolona33 = findViewById(R.id.kolona33);
        String kolona33 = "Kolona 33";
        tvKolona33.setText(kolona33);
        tvKolona34 = findViewById(R.id.kolona34);
        String kolona34 = "Kolona 34";
        tvKolona34.setText(kolona34);
        etKolonaOdgovor3 = findViewById(R.id.ET3);

        tvKolona41 = findViewById(R.id.kolona41);
        String kolona41 = "Kolona 41";
        tvKolona41.setText(kolona41);
        tvKolona42 = findViewById(R.id.kolona42);
        String kolona42 = "Kolona 42";
        tvKolona42.setText(kolona42);
        tvKolona43 = findViewById(R.id.kolona43);
        String kolona43 = "Kolona 43";
        tvKolona43.setText(kolona43);
        tvKolona44 = findViewById(R.id.kolona44);
        String kolona44 = "Kolona 44";
        tvKolona44.setText(kolona44);
        etKolonaOdgovor4 = findViewById(R.id.ET4);
        etOdgovorKonacno = findViewById(R.id.ETKonacno);

        btnAsocijacije = findViewById(R.id.BTNKonacnoAso);

        associoationId = 1;
        counterLifes = 0;
        associationDetails = databaseHelper.getAssociation(associoationId);
        correctAnswer = associationDetails[20];
        correctAnswer1 = associationDetails[4];
        correctAnswer2 = associationDetails[9];
        correctAnswer3 = associationDetails[14];
        correctAnswer4 = associationDetails[19];

        final boolean[] viewsClickable = {true};

        View.OnClickListener answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewsClickable[0]) {
                    switch (v.getId()) {
                        case R.id.kolona11:
                            tvKolona11.setText(associationDetails[0]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona12:
                            tvKolona12.setText(associationDetails[1]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona13:
                            tvKolona13.setText(associationDetails[2]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona14:
                            tvKolona14.setText(associationDetails[3]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona21:
                            tvKolona21.setText(associationDetails[5]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona22:
                            tvKolona22.setText(associationDetails[6]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona23:
                            tvKolona23.setText(associationDetails[7]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona24:
                            tvKolona24.setText(associationDetails[8]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona31:
                            tvKolona31.setText(associationDetails[10]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona32:
                            tvKolona32.setText(associationDetails[11]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona33:
                            tvKolona33.setText(associationDetails[12]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona34:
                            tvKolona34.setText(associationDetails[13]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona41:
                            tvKolona41.setText(associationDetails[15]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona42:
                            tvKolona42.setText(associationDetails[16]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona43:
                            tvKolona43.setText(associationDetails[17]);
                            viewsClickable[0] = false;
                            break;
                        case R.id.kolona44:
                            tvKolona44.setText(associationDetails[18]);
                            viewsClickable[0] = false;
                            break;
                    }
                }
            }
        };

        tvKolona11.setOnClickListener(answerClickListener);
        tvKolona12.setOnClickListener(answerClickListener);
        tvKolona13.setOnClickListener(answerClickListener);
        tvKolona14.setOnClickListener(answerClickListener);
        tvKolona21.setOnClickListener(answerClickListener);
        tvKolona22.setOnClickListener(answerClickListener);
        tvKolona23.setOnClickListener(answerClickListener);
        tvKolona24.setOnClickListener(answerClickListener);
        tvKolona31.setOnClickListener(answerClickListener);
        tvKolona32.setOnClickListener(answerClickListener);
        tvKolona33.setOnClickListener(answerClickListener);
        tvKolona34.setOnClickListener(answerClickListener);
        tvKolona41.setOnClickListener(answerClickListener);
        tvKolona42.setOnClickListener(answerClickListener);
        tvKolona43.setOnClickListener(answerClickListener);
        tvKolona44.setOnClickListener(answerClickListener);

        btnAsocijacije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewsClickable[0] = true;
                checkAnswer(etKolonaOdgovor1.getText().toString(), etKolonaOdgovor2.getText().toString(),
                        etKolonaOdgovor3.getText().toString(), etKolonaOdgovor4.getText().toString(),etOdgovorKonacno.getText().toString());
            }
        });
    }


    private void checkAnswer(String kolonaOdogovor1, String kolonaOdogovor2, String kolonaOdogovor3, String kolonaOdogovor4, String konacniOdogovor) {
        // Proveri da li je odabrani odgovor tačan

        String answer1 = kolonaOdogovor1.toUpperCase();
        String answer2 = kolonaOdogovor2.toUpperCase();
        String answer3 = kolonaOdogovor3.toUpperCase();
        String answer4 = kolonaOdogovor4.toUpperCase();
        String answer5 = konacniOdogovor.toUpperCase();
        if (answer5.equals(correctAnswer)) {
            tvKolona11.setText(associationDetails[0]);
            tvKolona12.setText(associationDetails[1]);
            tvKolona13.setText(associationDetails[2]);
            tvKolona14.setText(associationDetails[3]);
            etKolonaOdgovor1.setText(associationDetails[4]);

            tvKolona21.setText(associationDetails[5]);
            tvKolona22.setText(associationDetails[6]);
            tvKolona23.setText(associationDetails[7]);
            tvKolona24.setText(associationDetails[8]);
            etKolonaOdgovor2.setText(associationDetails[9]);

            tvKolona31.setText(associationDetails[10]);
            tvKolona32.setText(associationDetails[11]);
            tvKolona33.setText(associationDetails[12]);
            tvKolona34.setText(associationDetails[13]);
            etKolonaOdgovor3.setText(associationDetails[14]);

            tvKolona41.setText(associationDetails[15]);
            tvKolona42.setText(associationDetails[16]);
            tvKolona43.setText(associationDetails[17]);
            tvKolona44.setText(associationDetails[18]);
            etKolonaOdgovor4.setText(associationDetails[19]);
            Toast.makeText(this, "Čestitamo! "+correctAnswer+" je tačno!", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(AsocijacijeActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);

        } else if (answer1.equals(correctAnswer1)){
            tvKolona11.setText(associationDetails[0]);
            tvKolona12.setText(associationDetails[1]);
            tvKolona13.setText(associationDetails[2]);
            tvKolona14.setText(associationDetails[3]);
            etKolonaOdgovor1.setText("1."+associationDetails[4]);
            etKolonaOdgovor1.setEnabled(false);
        } else if(answer2.equals(correctAnswer2)){
            tvKolona21.setText(associationDetails[5]);
            tvKolona22.setText(associationDetails[6]);
            tvKolona23.setText(associationDetails[7]);
            tvKolona24.setText(associationDetails[8]);
            etKolonaOdgovor2.setText("2."+associationDetails[9]);
            etKolonaOdgovor2.setEnabled(false);
        } else if(answer3.equals(correctAnswer3)){
            tvKolona31.setText(associationDetails[10]);
            tvKolona32.setText(associationDetails[11]);
            tvKolona33.setText(associationDetails[12]);
            tvKolona34.setText(associationDetails[13]);
            etKolonaOdgovor3.setText("3."+associationDetails[14]);
            etKolonaOdgovor3.setEnabled(false);
        } else if(answer4.equals(correctAnswer4)){
            tvKolona41.setText(associationDetails[15]);
            tvKolona42.setText(associationDetails[16]);
            tvKolona43.setText(associationDetails[17]);
            tvKolona44.setText(associationDetails[18]);
            etKolonaOdgovor4.setText("4."+associationDetails[19]);
            etKolonaOdgovor4.setEnabled(false);
        } else {
            counterLifes++;
            if (counterLifes == 5){
                Toast.makeText(this, "Niste pogodili! "+correctAnswer+" je konacan odgovor!", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AsocijacijeActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }
        }

    }
}