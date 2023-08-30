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
import com.example.slagalica.LoginActivity;
import com.example.slagalica.R;
import com.example.slagalica.RegistrationActivity;
import com.example.slagalica.dataBase.DBHelper;

public class ResultActivity extends AppCompatActivity {

    private TextView resultView;
    private EditText editTextGuset;
    private Button btnBackToHome;

    private DBHelper dbHelper;

    private String checkGuest = "guest";
    private String guest;
    private int bodovi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        dbHelper = new DBHelper(this);

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);

        resultView = findViewById(R.id.TVResult);
        resultView.setText(String.valueOf(bodovi));

        editTextGuset = findViewById(R.id.ETResultGuest);
        btnBackToHome = findViewById(R.id.BTNResultGuest);


        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick = editTextGuset.getText().toString();
                if (nick.isEmpty()) {
                    nick = "";
                }
                if (nick == ("")) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ResultActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);
                } else {
                    dbHelper.insertResultGuest(nick, bodovi);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Uspjesno ste upisali svoj rezultat.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResultActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);
                }
            }
        });
    }
}