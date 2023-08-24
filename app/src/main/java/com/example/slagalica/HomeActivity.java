package com.example.slagalica;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;


import com.example.slagalica.dataBase.ConnectionService;
import com.example.slagalica.dataBase.NetworkManager;
import com.example.slagalica.games.AsocijacijeActivity;
import com.example.slagalica.games.KoZnaZnaActivity;
import com.example.slagalica.games.KorakPoKorakActivity;
import com.example.slagalica.games.MojBrojActivity;
import com.example.slagalica.games.SkockoActivity;
import com.example.slagalica.games.SpojniceActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HomeActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    String databaseUrl = "https://slagalica-76836-default-rtdb.europe-west1.firebasedatabase.app/";
    String clientValue;
    String serverValue;


    private DrawerLayout drawerLayout;
    private ConnectionService connectionService;
    private boolean isBound = false;

    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Button btnZapocni;
    private Button btnKoZnaZna;
    private Button btnKorak;
    private Button btnAso;
    private Button btnSkocko;

    private Button btnSpojnice;


    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private static final int DELAY_CHECK_HOST = 2000;
    private Handler handler = new Handler();

    private String host ="";
    private String klijent="" ;


    private NetworkManager networkManager;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectionService.ConnectionServiceBinder binder = (ConnectionService.ConnectionServiceBinder) service;
            connectionService = binder.getService();
            isBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connectionService = null;
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference();

        clientValue = "";
        serverValue = "";


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnZapocni = findViewById(R.id.BTNzapocniIgru);
        btnKoZnaZna = findViewById(R.id.BTNKoznaZna);
        btnKorak = findViewById(R.id.BTNKorakPoKorak);
        btnAso = findViewById(R.id.BTNAso);
        btnSkocko = findViewById(R.id.BTNSkocko);
        btnSpojnice = findViewById(R.id.BTNSpojnice);


        // Postavljanje toggle dugmeta za Navigation Drawer
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, android.R.string.ok, android.R.string.ok);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Obrada klikova na stavke navigacije
                int itemId = item.getItemId();

                if (itemId == R.id.menu_profile) {
                    // Logika za otvaranje profila
                    openProfile();
                } else if (itemId == R.id.menu_home) {
                    openHome();
                } else if (itemId == R.id.menu_leaderboard) {
                    // Logika za otvaranje rang liste
                    openLeaderboard();
                } else if (itemId == R.id.menu_logout) {
                    // Logika za odjavu
                    logout();
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

        btnZapocni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("server").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        if (value.equals("0")) {
                            databaseReference.child("server").setValue("1");
                            host ="host";
                            btnZapocni.setEnabled(false);
                            return;
                        } else if (value.equals("1")) {
                            databaseReference.child("client").setValue("1");
                            klijent = "klijent";
                            btnZapocni.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        databaseReference.child("client").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                clientValue = dataSnapshot.getValue(String.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        databaseReference.child("server").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                serverValue = dataSnapshot.getValue(String.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                databaseReference.child("client").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Toast.makeText(HomeActivity.this, "Ja sam " + klijent + host, Toast.LENGTH_SHORT).show();

                                        if (!serverValue.equals("") && !clientValue.equals("") && serverValue != null && clientValue != null && serverValue.equals(clientValue)) {
                                            Intent intent = new Intent(HomeActivity.this, MojBrojActivity.class);
                                            intent.putExtra("host",host);
                                            intent.putExtra("klijent",klijent);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                            }
                        }, 5000);


                    }
                }, 5000);



            }
        });

        btnKoZnaZna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, KoZnaZnaActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnKorak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, KorakPoKorakActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnAso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, AsocijacijeActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btnSkocko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, SkockoActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btnSpojnice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, SpojniceActivity.class);
                startActivity(intent);
                finish();

            }

        });

    }

    private void openProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void openHome() {
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void openLeaderboard() {

    }

    private void logout() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isHost() {

        boolean isServer = getIntent().getBooleanExtra("IS_SERVER", false);

        if (!isServer) {
            try {
                final int timeout = 3000; // 5000 milisekundi (5 sekundi)
                final int port = 1234; // Odaberite prikladni port

                AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        try {
                            Socket testSocket = new Socket();
                            testSocket.connect(new InetSocketAddress("192.168.0.12", port), timeout); // Zamijenite sa stvarnom IP adresom domaćina (servera)
                            testSocket.close();
                            return true;
                        } catch (IOException e) {
                            return false;
                        }
                    }
                };
                return task.execute().get(); // Pokreće se asinkroni zadatak i čeka se rezultat
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void startGameActivity() {
        Intent intent = new Intent(HomeActivity.this, MojBrojActivity.class);
        //   intent.putExtra("isHost", isHost);
        startActivity(intent);
        //finish(); // Zatvorite trenutnu aktivnost ako više nije potrebna
    }

    private void waitForClient() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (networkManager.isHost() && networkManager.getClientSocket() != null && networkManager.getClientSocket().isConnected()) {
                    // Klijent se pridružio, prebaci se na prvu igru
                    startGameActivity();
                } else {
                    // Nema pridruženog klijenta, nastavi čekati
                    waitForClient();
                }
            }
        }, 1000);
    }

}