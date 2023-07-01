package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.slagalica.HomeActivity;
import com.example.slagalica.LoginActivity;
import com.example.slagalica.R;

public class ResultActivity extends AppCompatActivity {

    private TextView resultView;


    private String checkGuest = "guest";
    private String guest;
    private int bodovi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);

        resultView = findViewById(R.id.TVResult);
        resultView.setText(String.valueOf(bodovi));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(ResultActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}