package com.example.slagalica.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ResultActivity extends AppCompatActivity {

    private TextView resultView;
    private TextView tvProtivnik;
    private TextView tvKonacni;
    private EditText editTextGuset;
    private Button btnBackToHome;

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";

    private DBHelper dbHelper;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private String checkGuest = "guest";
    private String WhoImI = "";
    private String guest;
    private int bodovi;
    private int poeni1 = 0;
    private int poeni2 = 0;

    private ValueEventListener bodoviKlijenta = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            poeni2 = dataSnapshot.getValue(Integer.class);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private ValueEventListener bodoviHosta = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            poeni1 = dataSnapshot.getValue(Integer.class);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        dbHelper = new DBHelper(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        guest = getIntent().getStringExtra("user");
        bodovi = getIntent().getIntExtra("bodovi", 0);
        WhoImI = getIntent().getStringExtra("WhoImI");

        if (WhoImI == null) {
            WhoImI = "";
        }
        if (guest == null) {
            guest = "";
        }

        databaseReference.child("igra").child("bodovi1").addValueEventListener(bodoviHosta);
        databaseReference.child("igra").child("bodovi2").addValueEventListener(bodoviKlijenta);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        String email = sharedPreferences.getString("currentUser", "");
        Query query = usersRef.orderByChild("email").equalTo(email);


        resultView = findViewById(R.id.TVResult);
        tvProtivnik = findViewById(R.id.TVProtivnik);
        tvKonacni = findViewById(R.id.TVKonacniRezultat);
        resultView.setText("Vasi bodovi: " + String.valueOf(bodovi));

        editTextGuset = findViewById(R.id.ETResultGuest);
        btnBackToHome = findViewById(R.id.BTNResultGuest);

        if (!guest.equals(checkGuest)) {
            editTextGuset.setVisibility(View.INVISIBLE);
            if (WhoImI.equals("klijent")) {
                databaseReference.child("igra").child("bodovi2").setValue(bodovi);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvProtivnik.setText("Protivnikovi bodovi: " + String.valueOf(poeni1));
                        if (bodovi > poeni1) {
                            tvKonacni.setText("Cestitamo na pobijedi.");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Integer brojPobjeda = userSnapshot.child("brojPobjeda").getValue(Integer.class);
                                            brojPobjeda++;
                                            databaseReference.child("users").child(user.getUid()).child("brojPobjeda").setValue(brojPobjeda);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Došlo je do greške pri proveri e-maila.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (bodovi < poeni1) {
                            tvKonacni.setText("Nazalost, protivnik vas je nadmudrio.");
                        } else if (bodovi == poeni1) {
                            tvKonacni.setText("Izjednaceni ste sa protivnikom.");
                        }
                    }
                }, 2000);
            } else if (WhoImI.equals("host")) {
                databaseReference.child("igra").child("bodovi1").setValue(bodovi);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvProtivnik.setText("Protivnikovi bodovi: " + String.valueOf(poeni2));
                        if (bodovi > poeni2) {
                            tvKonacni.setText("Cestitamo na pobijedi.");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Integer brojPobjeda = userSnapshot.child("brojPobjeda").getValue(Integer.class);
                                            brojPobjeda++;
                                            databaseReference.child("users").child(user.getUid()).child("brojPobjeda").setValue(brojPobjeda);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Došlo je do greške pri proveri e-maila.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (bodovi < poeni2) {
                            tvKonacni.setText("Nazalost, protivnik vas je nadmudrio.");
                        } else if (bodovi == poeni2) {
                            tvKonacni.setText("Izjednaceni ste sa protivnikom.");
                        }
                    }
                }, 2000);
            }
        }


        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guest.equals(checkGuest)) {
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
                } else if(!WhoImI.equals("")){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editor.remove("mojProtivnik2");
                            editor.remove("mojProtivnik1");
                            editor.remove("mojProtivnik");
                            editor.apply();
                            Intent intent = new Intent(ResultActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        databaseReference.child("igra").child("bodovi1").removeEventListener(bodoviHosta);
        databaseReference.child("igra").child("bodovi2").removeEventListener(bodoviKlijenta);
        super.onDestroy();
    }
}