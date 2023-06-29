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
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.slagalica.games.AsocijacijeActivity;
import com.example.slagalica.games.KoZnaZnaActivity;
import com.example.slagalica.games.KorakPoKorakActivity;
import com.example.slagalica.games.MojBrojActivity;
import com.example.slagalica.games.SkockoActivity;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Button btnZapocni;
    private Button btnKoZnaZna;
    private Button btnKorak;
    private Button btnAso;
    private Button btnSkocko;


    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private static final int DELAY_CHECK_HOST = 5000; // 5000 milisekundi (5 sekundi)
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnZapocni = findViewById(R.id.BTNzapocniIgru);
        btnKoZnaZna = findViewById(R.id.BTNKoznaZna);
        btnKorak = findViewById(R.id.BTNKorakPoKorak);
        btnAso = findViewById(R.id.BTNAso);
        btnSkocko = findViewById(R.id.BTNSkocko);

        // Postavljanje toggle dugmeta za Navigation Drawer
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, android.R.string.ok, android.R.string.ok);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Obrada klikova na stavke navigacije
                int itemId = item.getItemId();

                if (itemId == R.id.menu_profile) {
                    // Logika za otvaranje profila
                    openProfile();
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
                // Pozivanje metoda za uspostavljanje veze
                // Pozivanje metode isHost() nakon 5 sekundi
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean isHost = isHost();
                        if (isHost) {
                            connectToServer();
                        } else {
                            startServer();

                        }
                    }
                }, DELAY_CHECK_HOST);

                //    Intent intent = new Intent(HomeActivity.this, MojBrojActivity.class);
               //     startActivity(intent);
            }
        });

        btnKoZnaZna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, KoZnaZnaActivity .class);
                startActivity(intent);
            }
        });

        btnKorak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, KorakPoKorakActivity.class);
                startActivity(intent);
            }
        });

        btnAso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, AsocijacijeActivity.class);
                startActivity(intent);
            }
        });

        btnSkocko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, SkockoActivity.class);
                startActivity(intent);
            }
        });
    }


    private void openProfile() {
        // Implementacija otvaranja profila
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void openLeaderboard() {
        // Implementacija otvaranja rang liste
    }

    private void logout() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isHost() {
        try {
            final int timeout = 5000; // 5000 milisekundi (5 sekundi)
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
    }

    private void startServer() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    serverSocket = new ServerSocket(1234); // Odaberite prikladni port
                    clientSocket = serverSocket.accept();
                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    output = new PrintWriter(clientSocket.getOutputStream(), true);

                    // Primanje i slanje poruka

                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startGameActivity(true); // Pokreni novu aktivnost kao domaćin (server)
                    }
                });
            }
        };

        task.execute(); // Pokreće se asinkroni zadatak
    }

    private void connectToServer() {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    String serverIp = "192.168.0.12"; // Zamijenite s IP adresom domaćina (servera)
                    int port = 1234; // Zamijenite s odgovarajućim portom

                    clientSocket = new Socket(serverIp, port);

                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    output = new PrintWriter(clientSocket.getOutputStream(), true);

                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean isConnected) {
                if (isConnected) {
                    startGameActivity(false); // Pokreni novu aktivnost kao klijent
                } else {
                    // Neuspjeh pri povezivanju s serverom
                    // Ovdje možete dodati odgovarajuće postupke u slučaju neuspjeha
                }
            }
        };

        task.execute();
    }

    private void sendMessage(String message) {
        output.println(message);
    }

    // Ostatak koda vaše aktivnosti...

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }

            if (clientSocket != null) {
                clientSocket.close();
            }

            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Uklanjanje odgođenog zadatka ako je aktivnost uništena prije isteka vremena
        handler.removeCallbacksAndMessages(null);
    }
    private void startGameActivity(boolean isHost) {
        Intent intent = new Intent(HomeActivity.this, MojBrojActivity.class);
        intent.putExtra("isHost", isHost);
        startActivity(intent);
        finish(); // Zatvorite trenutnu aktivnost ako više nije potrebna
    }
}