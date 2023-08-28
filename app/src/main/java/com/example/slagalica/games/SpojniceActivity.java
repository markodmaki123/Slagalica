package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.List;

public class SpojniceActivity extends AppCompatActivity {

    private DBHelper databaseHelper;

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";


    private TextView tvSpojica11;
    private TextView tvSpojica12;
    private TextView tvSpojica13;
    private TextView tvSpojica14;
    private TextView tvSpojica15;

    private TextView tvSpojica21;
    private TextView tvSpojica22;
    private TextView tvSpojica23;
    private TextView tvSpojica24;
    private TextView tvSpojica25;

    private TextView timerView;
    private TextView bodoviView;
    private CountDownTimer timer;
    private long startTimer = 60000;

    private String checkGuest = "guest";
    private String guest;
    private int bodovi;

    String zapocniIgru = "";

    private String WhoImI="";

    private int connectionCounter;
    private int correctCounter;

    private int connectionId;

    private ValueEventListener prebacivajeIgre = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String value = dataSnapshot.getValue(String.class);
            zapocniIgru = value;
            if (value.equals("2")) {
                Intent intent = new Intent(SpojniceActivity.this, SkockoActivity.class);
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
                Intent intent = new Intent(SpojniceActivity.this, SpojniceActivity.class);
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spojnice);

        databaseHelper = new DBHelper(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();

        databaseReference.child("Spojnice").child("DrugaKolonaPrvoPolje").setValue("");
        databaseReference.child("Spojnice").child("DrugaKolonaDrugoPolje").setValue("");
        databaseReference.child("Spojnice").child("DrugaKolonaTrecePolje").setValue("");
        databaseReference.child("Spojnice").child("DrugaKolonaCetvrtoPolje").setValue("");
        databaseReference.child("Spojnice").child("DrugaKolonaPetoPolje").setValue("");

        databaseReference.child("Spojnice").child("PrvaKolonaPrvoPolje").setValue("");
        databaseReference.child("Spojnice").child("PrvaKolonaDrugoPolje").setValue("");
        databaseReference.child("Spojnice").child("PrvaKolonaTrecePolje").setValue("");
        databaseReference.child("Spojnice").child("PrvaKolonaCetvrtoPolje").setValue("");
        databaseReference.child("Spojnice").child("PrvaKolonaPetoPolje").setValue("");

        databaseReference.child("Spojnice").child("Brojac").setValue(0);
        databaseReference.child("Spojnice").child("BrojacTacnih").setValue(0);

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);
        WhoImI = getIntent().getStringExtra("WhoImI");

        if (guest == null) {
            guest = "";
        }
        if (WhoImI == null) {
            WhoImI = "";
        }

        // Ubacite pitanja u bazu podataka
        databaseHelper.insertConnections("Novak;Djokovic",
                "Rafael;Nadal",
                "Roger;Federer",
                "Carlos;Alcaraz",
                "Andrew;Murray"
        );

        tvSpojica11 = findViewById(R.id.kolona11);
        tvSpojica12 = findViewById(R.id.kolona12);
        tvSpojica13 = findViewById(R.id.kolona13);
        tvSpojica14 = findViewById(R.id.kolona14);
        tvSpojica15 = findViewById(R.id.kolona15);
        tvSpojica21 = findViewById(R.id.kolona21);
        tvSpojica22 = findViewById(R.id.kolona22);
        tvSpojica23 = findViewById(R.id.kolona23);
        tvSpojica24 = findViewById(R.id.kolona24);
        tvSpojica25 = findViewById(R.id.kolona25);

        timerView = findViewById(R.id.TVTimer);
        bodoviView = findViewById(R.id.TVBodovi);

        bodoviView.setText(String.valueOf(bodovi));

        connectionId = 1;
        connectionCounter = 0;
        correctCounter = 0;
        String[] connectionDetails = databaseHelper.getConnections(connectionId);
        displayConnections(connectionDetails);

        startTimer(startTimer);

        databaseReference.child("zapocniIgru").addValueEventListener(prebacivajeIgre);

        databaseReference.child("Spojnice").child("Brojac").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                connectionCounter = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        databaseReference.child("Spojnice").child("BrojacTacnih").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                correctCounter = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        View.OnClickListener answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.kolona21:
                        int textViewId1 = getResources().getIdentifier("kolona" + "1" + (connectionCounter+1), "id", getPackageName());
                        TextView textView1 = findViewById(textViewId1);
                        updateFirstColumn(connectionDetails[connectionCounter],textView1.getText().toString(),tvSpojica21.getText().toString(),v);
                        break;
                    case R.id.kolona22:
                        int textViewId2 = getResources().getIdentifier("kolona" + "1" + (connectionCounter+1), "id", getPackageName());
                        TextView textView2 = findViewById(textViewId2);
                        updateFirstColumn(connectionDetails[connectionCounter],textView2.getText().toString(),tvSpojica22.getText().toString(),v);
                        break;
                    case R.id.kolona23:
                        int textViewId3 = getResources().getIdentifier("kolona" + "1" + (connectionCounter+1), "id", getPackageName());
                        TextView textView3 = findViewById(textViewId3);
                        updateFirstColumn(connectionDetails[connectionCounter],textView3.getText().toString(),tvSpojica23.getText().toString(),v);
                        break;
                    case R.id.kolona24:
                        int textViewId4 = getResources().getIdentifier("kolona" + "1" + (connectionCounter+1), "id", getPackageName());
                        TextView textView4 = findViewById(textViewId4);
                        updateFirstColumn(connectionDetails[connectionCounter],textView4.getText().toString(),tvSpojica24.getText().toString(),v);
                        break;
                    case R.id.kolona25:
                        int textViewId5 = getResources().getIdentifier("kolona" + "1" + (connectionCounter+1), "id", getPackageName());
                        TextView textView5 = findViewById(textViewId5);
                        updateFirstColumn(connectionDetails[connectionCounter],textView5.getText().toString(),tvSpojica25.getText().toString(),v);
                        break;
                }
            }
        };

        tvSpojica21.setOnClickListener(answerClickListener);
        tvSpojica22.setOnClickListener(answerClickListener);
        tvSpojica23.setOnClickListener(answerClickListener);
        tvSpojica24.setOnClickListener(answerClickListener);
        tvSpojica25.setOnClickListener(answerClickListener);
    }

    private void displayConnections(String[] connectionDetails) {

        String connection1 = connectionDetails[0];
        String connection2 = connectionDetails[1];
        String connection3 = connectionDetails[2];
        String connection4 = connectionDetails[3];
        String connection5 = connectionDetails[4];

        String[] kolona11 = connection1.split(";");
        String[] kolona22 = connection2.split(";");
        String[] kolona33 = connection3.split(";");
        String[] kolona44 = connection4.split(";");
        String[] kolona55 = connection5.split(";");

        List<String> random = new ArrayList<String>();

        if(WhoImI.equals("host")) {
            random.add(kolona11[1]);
            random.add(kolona22[1]);
            random.add(kolona33[1]);
            random.add(kolona44[1]);
            random.add(kolona55[1]);
            Collections.shuffle(random);

            databaseReference.child("Spojnice").child("DrugaKolonaPrvoPolje").setValue(random.get(0));
            databaseReference.child("Spojnice").child("DrugaKolonaDrugoPolje").setValue(random.get(1));
            databaseReference.child("Spojnice").child("DrugaKolonaTrecePolje").setValue(random.get(2));
            databaseReference.child("Spojnice").child("DrugaKolonaCetvrtoPolje").setValue(random.get(3));
            databaseReference.child("Spojnice").child("DrugaKolonaPetoPolje").setValue(random.get(4));

            tvSpojica11.setText(kolona11[0]);
            tvSpojica21.setText(random.get(0));
            tvSpojica12.setText(kolona22[0]);
            tvSpojica22.setText(random.get(1));
            tvSpojica13.setText(kolona33[0]);
            tvSpojica23.setText(random.get(2));
            tvSpojica14.setText(kolona44[0]);
            tvSpojica24.setText(random.get(3));
            tvSpojica15.setText(kolona55[0]);
            tvSpojica25.setText(random.get(4));
        }
        else if(WhoImI.equals("klijent")){
            databaseReference.child("Spojnice").child("DrugaKolonaPrvoPolje").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    assert value != null;
                    if(!value.equals("")) {
                        random.add(value);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            databaseReference.child("Spojnice").child("DrugaKolonaDrugoPolje").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    assert value != null;
                    if(!value.equals("")) {
                        random.add(value);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            databaseReference.child("Spojnice").child("DrugaKolonaTrecePolje").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    assert value != null;
                    if(!value.equals("")) {
                        random.add(value);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            databaseReference.child("Spojnice").child("DrugaKolonaCetvrtoPolje").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    assert value != null;
                    if(!value.equals("")) {
                        random.add(value);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            databaseReference.child("Spojnice").child("DrugaKolonaPetoPolje").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    assert value != null;
                    if(!value.equals("")) {
                        random.add(value);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            tvSpojica11.setText(kolona11[0]);
            tvSpojica21.setText(random.get(0));
            tvSpojica21.setEnabled(false);
            tvSpojica12.setText(kolona22[0]);
            tvSpojica22.setText(random.get(1));
            tvSpojica22.setEnabled(false);
            tvSpojica13.setText(kolona33[0]);
            tvSpojica23.setText(random.get(2));
            tvSpojica23.setEnabled(false);
            tvSpojica14.setText(kolona44[0]);
            tvSpojica24.setText(random.get(3));
            tvSpojica24.setEnabled(false);
            tvSpojica15.setText(kolona55[0]);
            tvSpojica25.setText(random.get(4));
            tvSpojica25.setEnabled(false);
        }




    }

    private void updateFirstColumn(String correctConnections, String firstConnection, String secondConnecion, View v){
        String[] correct = correctConnections.split(";");

        if(correct[0].equals(firstConnection) && correct[1].equals(secondConnecion)){
            int textViewId = getResources().getIdentifier("kolona" + 1 + (connectionCounter+1), "id", getPackageName());
            TextView textView = findViewById(textViewId);
            textView.setBackgroundColor(Color.GREEN);
            v.setBackgroundColor(Color.GREEN);
            v.setEnabled(false);

            if(!guest.equals(checkGuest)) {
                switch (v.getId()) {
                    case R.id.kolona21:
                        databaseReference.child("Spojnice").child("PrvaKolonaPrvoPolje").child("BrojDrugogOdgovora").setValue("21");
                        break;
                    case R.id.kolona22:
                        databaseReference.child("Spojnice").child("PrvaKolonaDrugoPolje").child("BrojDrugogOdgovora").setValue("22");
                        break;
                    case R.id.kolona23:
                        databaseReference.child("Spojnice").child("PrvaKolonaTrecePolje").child("BrojDrugogOdgovora").setValue("23");
                        break;
                    case R.id.kolona24:
                        databaseReference.child("Spojnice").child("PrvaKolonaCetvrtoPolje").child("BrojDrugogOdgovora").setValue("24");
                        break;
                    case R.id.kolona25:
                        databaseReference.child("Spojnice").child("PrvaKolonaPetoPolje").child("BrojDrugogOdgovora").setValue("25");
                        break;
                }
                switch (textView.getText().toString()) {
                    case "Novak":
                        databaseReference.child("Spojnice").child("PrvaKolonaPrvoPolje").setValue("tacno");
                        break;
                    case "Rafael":
                        databaseReference.child("Spojnice").child("PrvaKolonaDrugoPolje").setValue("tacno");
                        break;
                    case "Carlos":
                        databaseReference.child("Spojnice").child("PrvaKolonaTrecePolje").setValue("tacno");
                        break;
                    case "Andrew":
                        databaseReference.child("Spojnice").child("PrvaKolonaCetvrtoPolje").setValue("tacno");
                        break;
                    case "Roger":
                        databaseReference.child("Spojnice").child("PrvaKolonaPetoPolje").setValue("tacno");
                        break;
                }
            }
            bodovi = bodovi + 2;
            bodoviView.setText(String.valueOf(bodovi));
            correctCounter++;
            databaseReference.child("Spojnice").child("BrojacTacnih").setValue(correctCounter);
        } else {
            int textViewId = getResources().getIdentifier("kolona" + 1 + (connectionCounter+1), "id", getPackageName());
            TextView textView = findViewById(textViewId);
            textView.setBackgroundColor(Color.RED);
            if(!guest.equals(checkGuest)) {
                switch (v.getId()) {
                    case R.id.kolona11:
                        databaseReference.child("Spojnice").child("PrvaKolonaPrvoPolje").setValue("netacno");
                        break;
                    case R.id.kolona12:
                        databaseReference.child("Spojnice").child("PrvaKolonaDrugoPolje").setValue("netacno");
                        break;
                    case R.id.kolona13:
                        databaseReference.child("Spojnice").child("PrvaKolonaTrecePolje").setValue("netacno");
                        break;
                    case R.id.kolona14:
                        databaseReference.child("Spojnice").child("PrvaKolonaCetvrtoPolje").setValue("netacno");
                        break;
                    case R.id.kolona15:
                        databaseReference.child("Spojnice").child("PrvaKolonaPetoPolje").setValue("netacno");
                        break;
                }
            }
        }

        if(connectionCounter==4){
            bodoviView.setText(String.valueOf(bodovi));
            timer.cancel();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!guest.equals(checkGuest)){
                        if(WhoImI.equals("host")){
                            tvSpojica21.setEnabled(false);
                            tvSpojica22.setEnabled(false);
                            tvSpojica23.setEnabled(false);
                            tvSpojica24.setEnabled(false);
                            tvSpojica25.setEnabled(false);
                            if(correctCounter==4){
                                if (zapocniIgru.equals("0")) {
                                    databaseReference.child("zapocniIgru").setValue("1");
                                } else if (zapocniIgru.equals("1.5")) {
                                    databaseReference.child("zapocniIgru").setValue("2");
                                }
                            }
                        }
                    } else {
                        Intent intent = new Intent(SpojniceActivity.this, AsocijacijeActivity.class);
                        intent.putExtra("user", "guest");
                        intent.putExtra("bodovi", bodovi);
                        startActivity(intent);
                        finish();
                    }
                }
            }, 3000);
        } else {
            connectionCounter++;
            databaseReference.child("Spojnice").child("Brojac").setValue(connectionCounter);
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
                timer.cancel();
                Toast.makeText(SpojniceActivity.this, "Vrijeme je isteklo!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SpojniceActivity.this, AsocijacijeActivity.class);
                intent.putExtra("user","guest");
                intent.putExtra("bodovi",bodovi);
                startActivity(intent);
                finish();
            }
        };

        timer.start();
    }

    private void updateTimerText(long millisUntilFinished) {
        int seconds = (int) (millisUntilFinished / 1000);

        String time = String.format("%02d", seconds);
        timerView.setText(time);
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        databaseReference.child("zapocniIgru").removeEventListener(prebacivajeIgre);
        super.onDestroy();
    }
}