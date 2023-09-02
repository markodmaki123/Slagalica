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
import java.util.Random;

public class SpojniceActivity extends AppCompatActivity {

    private DBHelper databaseHelper;

    private List<CountDownTimer> activeTimers = new ArrayList<>();
    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";


    private TextView tvSpojica11;
    private TextView tvSpojica12;
    private TextView tvSpojica13;
    private TextView tvSpojica14;
    private TextView tvSpojica15;
    private TextView tvProtinik;

    private TextView tvSpojica21;
    private TextView tvSpojica22;
    private TextView tvSpojica23;
    private TextView tvSpojica24;
    private TextView tvSpojica25;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private TextView timerView;
    private TextView bodoviView;
    private CountDownTimer timer;
    private long startTimer = 60000;

    String[] connectionDetails;

    private String checkGuest = "guest";
    private String guest;
    private int bodovi;

    String zapocniIgru = "";
    private String prvaKolona = "";
    private String drugaKolona = "";

    private String WhoImI = "";

    private int connectionCounter;
    private int correctCounter;
    private int listenerStartCounter = 0;

    private int connectionId;

    private String[] connections;

    private ValueEventListener DrugaKolonaPrvoPolje = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String value = dataSnapshot.getValue(String.class);
            assert value != null;
            if (!value.equals("")) {
                tvSpojica21.setText(value);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private ValueEventListener DrugaKolonaDrugoPolje = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String value = dataSnapshot.getValue(String.class);
            assert value != null;
            if (!value.equals("")) {
                tvSpojica22.setText(value);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private ValueEventListener DrugaKolonaTrecePolje = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String value = dataSnapshot.getValue(String.class);
            assert value != null;
            if (!value.equals("")) {
                tvSpojica23.setText(value);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private ValueEventListener DrugaKolonaCetvrtoPolje = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String value = dataSnapshot.getValue(String.class);
            assert value != null;
            if (!value.equals("")) {
                tvSpojica24.setText(value);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private ValueEventListener DrugaKolonaPetoPolje = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String value = dataSnapshot.getValue(String.class);
            assert value != null;
            if (!value.equals("")) {
                tvSpojica25.setText(value);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

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

    private ValueEventListener updateClientValues = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            prvaKolona = dataSnapshot.getValue(String.class);
            if (prvaKolona == null) {
                prvaKolona = "";
            }
            switch (prvaKolona) {
                case "tacno11": {
                    tvSpojica11.setBackgroundColor(Color.GREEN);
                    if (drugaKolona != null) {
                        colorTextView(drugaKolona);
                    }
                    break;
                }
                case "tacno12": {
                    tvSpojica12.setBackgroundColor(Color.GREEN);
                    if (drugaKolona != null) {
                        colorTextView(drugaKolona);
                    }
                    break;
                }
                case "tacno13": {
                    tvSpojica13.setBackgroundColor(Color.GREEN);
                    if (drugaKolona != null) {
                        colorTextView(drugaKolona);
                    }
                    break;
                }
                case "tacno14": {
                    tvSpojica14.setBackgroundColor(Color.GREEN);
                    if (drugaKolona != null) {
                        colorTextView(drugaKolona);
                    }
                    break;
                }
                case "tacno15": {
                    tvSpojica15.setBackgroundColor(Color.GREEN);
                    if (drugaKolona != null) {
                        colorTextView(drugaKolona);
                    }
                    break;
                }
                case "netacno11": {
                    tvSpojica11.setBackgroundColor(Color.RED);
                    break;
                }
                case "netacno12": {
                    tvSpojica12.setBackgroundColor(Color.RED);
                    break;
                }
                case "netacno13": {
                    tvSpojica13.setBackgroundColor(Color.RED);
                    break;
                }
                case "netacno14": {
                    tvSpojica14.setBackgroundColor(Color.RED);
                    break;
                }
                case "netacno15": {
                    tvSpojica15.setBackgroundColor(Color.RED);
                    break;
                }
            }
            databaseReference.child("Spojnice").child("PrvaKolona").removeEventListener(updateClientValues);
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

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        databaseReference.child("Spojnice").child("PrvaKolona").setValue("");
        databaseReference.child("Spojnice").child("BrojDrugogOdgovora").setValue("");


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

        databaseHelper.insertConnections("Max;Verstappen",
                "Lewis;Hamilton",
                "Michael;Schumacher",
                "Ayrton;Senna",
                "Kimi;Raikkonen"
        );

        databaseHelper.insertConnections("Beograd;Srbija",
                "Bukurest;Rumunija",
                "Paris;Francuska",
                "Moskva;Rusija",
                "Lisabon;Portugal"
        );

        databaseHelper.insertConnections("Lionel;Messi",
                "Erling;Haaland",
                "Kevin;DeBruyne",
                "Sergio;Ramos",
                "Khvicha;Kvaratskhelia"
        );

        tvProtinik = findViewById(R.id.TVProtivnik);
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

        String protivnik = sharedPreferences.getString("mojProtivnik", "");

        tvProtinik.setText("VS " + protivnik);

        if (WhoImI.equals("host")) {
            Random random = new Random();
            connectionId = random.nextInt(3) + 1;
            connectionCounter = 0;
            correctCounter = 0;
            connectionDetails = databaseHelper.getConnections(connectionId);
            connections = databaseHelper.getConnections(connectionId);
            databaseReference.child("Spojnice").child("idSpojnice").setValue(connectionId);
        } else if (WhoImI.equals("klijent")) {
            databaseReference.child("Spojnice").child("idSpojnice").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Integer value = dataSnapshot.getValue(Integer.class);
                    if (value != null) {
                        connectionDetails = databaseHelper.getConnections(value);
                        connections = databaseHelper.getConnections(value);
                        startSecondColomunListeners();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } else if (guest.equals(checkGuest)) {
            Random random = new Random();
            connectionId = random.nextInt(3) + 1;
            connectionCounter = 0;
            correctCounter = 0;
            connectionDetails = databaseHelper.getConnections(connectionId);
            connections = databaseHelper.getConnections(connectionId);
        }

        if (!WhoImI.equals("klijent")) {
            databaseReference.child("Spojnice").child("DrugaKolonaPrvoPolje").setValue("");
            databaseReference.child("Spojnice").child("DrugaKolonaDrugoPolje").setValue("");
            databaseReference.child("Spojnice").child("DrugaKolonaTrecePolje").setValue("");
            databaseReference.child("Spojnice").child("DrugaKolonaCetvrtoPolje").setValue("");
            databaseReference.child("Spojnice").child("DrugaKolonaPetoPolje").setValue("", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        if (WhoImI.equals("host")) {
                            displayConnections(connectionDetails);
                        }
                    }
                }
            });
        } else if (WhoImI.equals("klijent")) {
            databaseReference.child("Spojnice").child("DrugaKolonaPrvoPolje").addValueEventListener(DrugaKolonaPrvoPolje);
            databaseReference.child("Spojnice").child("DrugaKolonaDrugoPolje").addValueEventListener(DrugaKolonaDrugoPolje);
            databaseReference.child("Spojnice").child("DrugaKolonaTrecePolje").addValueEventListener(DrugaKolonaTrecePolje);
            databaseReference.child("Spojnice").child("DrugaKolonaCetvrtoPolje").addValueEventListener(DrugaKolonaCetvrtoPolje);
            databaseReference.child("Spojnice").child("DrugaKolonaPetoPolje").addValueEventListener(DrugaKolonaPetoPolje);
        }

        startTimer(startTimer);

        if (guest.equals(checkGuest)) {
            displayConnectionsGuest(connectionDetails);
        }

        databaseReference.child("zapocniIgru").addValueEventListener(prebacivajeIgre);

        if (!guest.equals(checkGuest)) {
            databaseReference.child("Spojnice").child("Brojac").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    connectionCounter = dataSnapshot.getValue(Integer.class);
                    if (connectionCounter > 0 && connectionCounter < 6) {
                        databaseReference.child("Spojnice").child("PrvaKolona").addValueEventListener(updateClientValues);
                        Toast.makeText(SpojniceActivity.this, String.valueOf(connectionCounter), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


        databaseReference.child("Spojnice").child("BrojDrugogOdgovora").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                drugaKolona = dataSnapshot.getValue(String.class);
                if (connectionCounter == 4) {
                    switch (drugaKolona) {
                        case "tacno21": {
                            tvSpojica21.setBackgroundColor(Color.GREEN);
                            break;
                        }
                        case "tacno22": {
                            tvSpojica22.setBackgroundColor(Color.GREEN);
                            break;
                        }
                        case "tacno23": {
                            tvSpojica23.setBackgroundColor(Color.GREEN);
                            break;
                        }
                        case "tacno24": {
                            tvSpojica24.setBackgroundColor(Color.GREEN);
                            break;
                        }
                        case "tacno25": {
                            tvSpojica25.setBackgroundColor(Color.GREEN);
                            break;
                        }
                    }
                }
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
                        int textViewId1 = getResources().getIdentifier("kolona" + "1" + (connectionCounter + 1), "id", getPackageName());
                        TextView textView1 = findViewById(textViewId1);
                        updateFirstColumn(connectionDetails[connectionCounter], textView1.getText().toString(), tvSpojica21.getText().toString(), v);
                        break;
                    case R.id.kolona22:
                        int textViewId2 = getResources().getIdentifier("kolona" + "1" + (connectionCounter + 1), "id", getPackageName());
                        TextView textView2 = findViewById(textViewId2);
                        updateFirstColumn(connectionDetails[connectionCounter], textView2.getText().toString(), tvSpojica22.getText().toString(), v);
                        break;
                    case R.id.kolona23:
                        int textViewId3 = getResources().getIdentifier("kolona" + "1" + (connectionCounter + 1), "id", getPackageName());
                        TextView textView3 = findViewById(textViewId3);
                        updateFirstColumn(connectionDetails[connectionCounter], textView3.getText().toString(), tvSpojica23.getText().toString(), v);
                        break;
                    case R.id.kolona24:
                        int textViewId4 = getResources().getIdentifier("kolona" + "1" + (connectionCounter + 1), "id", getPackageName());
                        TextView textView4 = findViewById(textViewId4);
                        updateFirstColumn(connectionDetails[connectionCounter], textView4.getText().toString(), tvSpojica24.getText().toString(), v);
                        break;
                    case R.id.kolona25:
                        int textViewId5 = getResources().getIdentifier("kolona" + "1" + (connectionCounter + 1), "id", getPackageName());
                        TextView textView5 = findViewById(textViewId5);
                        updateFirstColumn(connectionDetails[connectionCounter], textView5.getText().toString(), tvSpojica25.getText().toString(), v);
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

        if (WhoImI.equals("host")) {
            if (listenerStartCounter == 0) {
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
                listenerStartCounter++;
            }
        }
    }

    private void displayConnectionsGuest(String[] connectionDetails) {

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

        random.add(kolona11[1]);
        random.add(kolona22[1]);
        random.add(kolona33[1]);
        random.add(kolona44[1]);
        random.add(kolona55[1]);
        Collections.shuffle(random);

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

    private void updateFirstColumn(String correctConnections, String firstConnection, String secondConnecion, View v) {
        String[] correct = correctConnections.split(";");
        String[] expectedResult1 = connections[0].split(";");
        String[] expectedResult2 = connections[1].split(";");
        String[] expectedResult3 = connections[2].split(";");
        String[] expectedResult4 = connections[3].split(";");
        String[] expectedResult5 = connections[4].split(";");

        if (correct[0].equals(firstConnection) && correct[1].equals(secondConnecion)) {
            int textViewId = getResources().getIdentifier("kolona" + 1 + (connectionCounter + 1), "id", getPackageName());
            TextView textView = findViewById(textViewId);
            textView.setBackgroundColor(Color.GREEN);
            v.setBackgroundColor(Color.GREEN);
            v.setEnabled(false);
            if (!guest.equals(checkGuest)) {
                String text = textView.getText().toString();
                if (text.equals(expectedResult1[0])) {
                    databaseReference.child("Spojnice").child("PrvaKolona").setValue("tacno11");
                    colorAnswerForClient(v);
                } else if (text.equals(expectedResult2[0])) {
                    databaseReference.child("Spojnice").child("PrvaKolona").setValue("tacno12");
                    colorAnswerForClient(v);
                } else if (text.equals(expectedResult3[0])) {
                    databaseReference.child("Spojnice").child("PrvaKolona").setValue("tacno13");
                    colorAnswerForClient(v);
                } else if (text.equals(expectedResult4[0])) {
                    databaseReference.child("Spojnice").child("PrvaKolona").setValue("tacno14");
                    colorAnswerForClient(v);
                } else if (text.equals(expectedResult5[0])) {
                    databaseReference.child("Spojnice").child("PrvaKolona").setValue("tacno15");
                    colorAnswerForClient(v);
                }
            }
            bodovi = bodovi + 2;
            bodoviView.setText(String.valueOf(bodovi));
            correctCounter++;
            databaseReference.child("Spojnice").child("BrojacTacnih").setValue(correctCounter);
        } else {
            int textViewId = getResources().getIdentifier("kolona" + 1 + (connectionCounter + 1), "id", getPackageName());
            TextView textView = findViewById(textViewId);
            textView.setBackgroundColor(Color.RED);
            if (!guest.equals(checkGuest)) {
                String text = textView.getText().toString();
                if (text.equals(expectedResult1[0])) {
                    databaseReference.child("Spojnice").child("PrvaKolona").setValue("netacno11");
                } else if (text.equals(expectedResult2[0])) {
                    databaseReference.child("Spojnice").child("PrvaKolona").setValue("netacno12");
                } else if (text.equals(expectedResult3[0])) {
                    databaseReference.child("Spojnice").child("PrvaKolona").setValue("netacno13");
                } else if (text.equals(expectedResult4[0])) {
                    databaseReference.child("Spojnice").child("PrvaKolona").setValue("netacno14");
                } else if (text.equals(expectedResult5[0])) {
                    databaseReference.child("Spojnice").child("PrvaKolona").setValue("netacno15");
                }
            }
        }

        if (connectionCounter == 4) {
            bodoviView.setText(String.valueOf(bodovi));
            timer.cancel();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!guest.equals(checkGuest)) {
                        if (WhoImI.equals("host")) {
                            connectionCounter++;
                            tvSpojica21.setEnabled(false);
                            tvSpojica22.setEnabled(false);
                            tvSpojica23.setEnabled(false);
                            tvSpojica24.setEnabled(false);
                            tvSpojica25.setEnabled(false);
                            if (correctCounter == 5) {
                                if (zapocniIgru.equals("0")) {
                                    databaseReference.child("zapocniIgru").setValue("1");
                                } else if (zapocniIgru.equals("1.5")) {
                                    databaseReference.child("zapocniIgru").setValue("2");
                                }
                            }
                        } else if (WhoImI.equals("klijent")) {

                            if (correctCounter != 5) {
                                //if(tvSpojica21) uradi kad chatgpt pcone radit
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
            }, 2000);
        } else {
            connectionCounter++;
            databaseReference.child("Spojnice").child("Brojac").setValue(connectionCounter);
        }
    }

    private void colorAnswerForClient(View v) {
        switch (v.getId()) {
            case R.id.kolona21:
                databaseReference.child("Spojnice").child("BrojDrugogOdgovora").setValue("tacno21");
                break;
            case R.id.kolona22:
                databaseReference.child("Spojnice").child("BrojDrugogOdgovora").setValue("tacno22");
                break;
            case R.id.kolona23:
                databaseReference.child("Spojnice").child("BrojDrugogOdgovora").setValue("tacno23");
                break;
            case R.id.kolona24:
                databaseReference.child("Spojnice").child("BrojDrugogOdgovora").setValue("tacno24");
                break;
            case R.id.kolona25:
                databaseReference.child("Spojnice").child("BrojDrugogOdgovora").setValue("tacno25");
                break;
        }
    }

    private void colorTextView(String column) {
        switch (column) {
            case "tacno21":
                tvSpojica21.setBackgroundColor(Color.GREEN);
                break;
            case "tacno22":
                tvSpojica22.setBackgroundColor(Color.GREEN);
                break;
            case "tacno23":
                tvSpojica23.setBackgroundColor(Color.GREEN);
                break;
            case "tacno24":
                tvSpojica24.setBackgroundColor(Color.GREEN);
                break;
            case "tacno25":
                tvSpojica25.setBackgroundColor(Color.GREEN);
                break;
        }
    }

    private void startSecondColomunListeners() {
        String connection1 = connections[0];
        String connection2 = connections[1];
        String connection3 = connections[2];
        String connection4 = connections[3];
        String connection5 = connections[4];

        String[] kolona11 = connection1.split(";");
        String[] kolona22 = connection2.split(";");
        String[] kolona33 = connection3.split(";");
        String[] kolona44 = connection4.split(";");
        String[] kolona55 = connection5.split(";");

        tvSpojica11.setText(kolona11[0]);
        tvSpojica21.setEnabled(false);
        tvSpojica12.setText(kolona22[0]);
        tvSpojica22.setEnabled(false);
        tvSpojica13.setText(kolona33[0]);
        tvSpojica23.setEnabled(false);
        tvSpojica14.setText(kolona44[0]);
        tvSpojica24.setEnabled(false);
        tvSpojica15.setText(kolona55[0]);
        tvSpojica25.setEnabled(false);
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
                Toast.makeText(SpojniceActivity.this, "Vrijeme je isteklo!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SpojniceActivity.this, AsocijacijeActivity.class);
                intent.putExtra("user", "guest");
                intent.putExtra("bodovi", bodovi);
                startActivity(intent);
                finish();
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

    private void stopAllTimers() {
        for (CountDownTimer timer : activeTimers) {
            timer.cancel();
        }
        activeTimers.clear(); // Očisti listu aktivnih tajmera
    }

    @Override
    protected void onDestroy() {
        stopAllTimers();
        databaseReference.child("Spojnice").child("DrugaKolonaPetoPolje").removeEventListener(DrugaKolonaPetoPolje);
        databaseReference.child("Spojnice").child("DrugaKolonaCetvrtoPolje").removeEventListener(DrugaKolonaCetvrtoPolje);
        databaseReference.child("Spojnice").child("DrugaKolonaTrecePolje").removeEventListener(DrugaKolonaTrecePolje);
        databaseReference.child("Spojnice").child("DrugaKolonaDrugoPolje").removeEventListener(DrugaKolonaDrugoPolje);
        databaseReference.child("Spojnice").child("DrugaKolonaPrvoPolje").removeEventListener(DrugaKolonaPrvoPolje);
        databaseReference.child("PrvaKolona").removeEventListener(updateClientValues);
        databaseReference.child("zapocniIgru").removeEventListener(prebacivajeIgre);
        super.onDestroy();
    }
}