package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slagalica.HomeActivity;
import com.example.slagalica.R;
import com.example.slagalica.dataBase.DBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpojniceActivity extends AppCompatActivity {

    private DBHelper databaseHelper;


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

    private int connectionCounter;
    private int correctCounter;

    private int connectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spojnice);

        databaseHelper = new DBHelper(this);

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

        connectionId = 1;
        connectionCounter = 0;
        correctCounter = 0;
        String[] connectionDetails = databaseHelper.getConnections(connectionId);
        displayConnections(connectionDetails);

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

    private void updateFirstColumn(String correctConnections, String firstConnection, String secondConnecion, View v){
        String[] correct = correctConnections.split(";");

        if(correct[0].equals(firstConnection) && correct[1].equals(secondConnecion)){
            int textViewId = getResources().getIdentifier("kolona" + 1 + (connectionCounter+1), "id", getPackageName());
            TextView textView = findViewById(textViewId);
            textView.setBackgroundColor(Color.GREEN);
            v.setBackgroundColor(Color.GREEN);
            v.setEnabled(false);
            correctCounter++;
        } else {
            int textViewId = getResources().getIdentifier("kolona" + 1 + (connectionCounter+1), "id", getPackageName());
            TextView textView = findViewById(textViewId);
            textView.setBackgroundColor(Color.RED);
        }

        if(connectionCounter==4){
            Toast.makeText(this, "Igra se zavrsila. Pogodjenih spojnica: "+correctCounter+".", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SpojniceActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        } else {
            connectionCounter++;
        }
    }
}