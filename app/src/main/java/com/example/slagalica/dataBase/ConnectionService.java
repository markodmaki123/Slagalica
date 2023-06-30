package com.example.slagalica.dataBase;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.Toast;

import com.example.slagalica.games.MojBrojActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ConnectionService extends Service {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader input;

    private Map<String, String> messageMap;
    private Map<String, String> sentMap;
    private PrintWriter output;
    private ConnectionServiceBinder binder = new ConnectionServiceBinder();

    private Handler handler = new Handler(Looper.getMainLooper());

    public class ConnectionServiceBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        messageMap = new HashMap<>();
        sentMap = new HashMap<>();
        // Initialize your serverSocket, input, and output here
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void startGameActivity(boolean isServer) {
        // Start the game activity based on whether the service is acting as a server or client
        Intent intent = new Intent(getApplicationContext(), MojBrojActivity.class);
        intent.putExtra("IS_SERVER", !isServer);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startServer(boolean isServer) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    serverSocket = new ServerSocket(1234); // Choose an appropriate port
                    clientSocket = serverSocket.accept();
                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    output = new PrintWriter(clientSocket.getOutputStream(), true);

                    // Receive and send messages
                    String message;
                    while ((message = input.readLine()) != null) {
                        Log.d("Received from client", message);
                        // Handle the received message
                        Toast.makeText(ConnectionService.this, "FROM CLIENT : "+message, Toast.LENGTH_SHORT).show();
                        // Send a response back to the client
                        output.println("Response from server");
                        output.flush();
                    }

                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //startGameActivity(isServer); // or startGameActivity(false)
                    }
                });
            }
        };

        task.execute(); // Execute the asynchronous task
    }

    public void connectToServer(boolean isServer) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    String serverIp = "192.168.0.12"; // Replace with the host's IP address
                    int port = 1234; // Replace with the appropriate port

                    clientSocket = new Socket(serverIp, port);

                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    output = new PrintWriter(clientSocket.getOutputStream(), true);

                    // Receive and send messages
                    String message;
                    while ((message = input.readLine()) != null) {
                        Log.d("Received from server", message);
                        // Handle the received message
                        Toast.makeText(ConnectionService.this, "FROM SERVER: "+message, Toast.LENGTH_SHORT).show();
                        // Send a response back to the server
                        output.println("Response from client");
                        output.flush();
                    }

                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean isConnected) {
                if (isConnected) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //startGameActivity(isServer); // or startGameActivity(false)
                        }
                    });
                } else {
                    // Handle the case when the client is unable to connect to the server
                }
            }
        };

        task.execute(); // Execute the asynchronous task
    }


    public void sendMessage(String message) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (output != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(message);
                    output.println(json);
                }
                return null;
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void receiveMessage() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    return input.readLine(); // Receive the server message
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception if there is an error while receiving the message
                    return null; // Or return an appropriate error message
                }
            }

            @Override
            protected void onPostExecute(String message) {
                if (message != null) {
                    Toast.makeText(ConnectionService.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConnectionService.this, "Error occurred while receiving message", Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); // Execute the AsyncTask on a separate thread
    }

    public void receiveMessageMojBrojGUI(String message) {
        Gson gson = new Gson();
        Map<String, String> receivedMap = gson.fromJson(message, new TypeToken<Map<String, String>>() {
        }.getType());

        // Update the client's UI with the received GUI state
        for (String key : receivedMap.keySet()) {
            if (messageMap.containsKey(key)) {
                messageMap.put(key, receivedMap.get(key));
            }
        }
    }

    public Map<String, String> getMessageMap() {
        return messageMap;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Close your sockets and input/output streams here
    }
}