package com.example.slagalica.dataBase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private boolean isHost;

    public void startAsServer() {
        Thread serverThread = new Thread(new ServerRunnable());
        serverThread.start();
    }

    public void startAsClient() {
        Thread clientThread = new Thread(new ClientRunnable());
        clientThread.start();
    }

    public boolean isHost() {
        return isHost;
    }

    public void closeConnection() {
        try {
            if (clientSocket != null)
                clientSocket.close();

            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerRunnable implements Runnable {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(1234);
                clientSocket = serverSocket.accept();
                isHost = true;

                // Trenutni uređaj postaje poslužitelj (host)
                // Ovdje možete obavijestiti igrače da se pripreme za prvu igru
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientRunnable implements Runnable {
        @Override
        public void run() {
            try {
                clientSocket = new Socket("192.168.0.12", 1234);
                isHost = false;

                // Veza je uspostavljena, izvršite logiku za drugog igrača
                // Ovdje možete obavijestiti igrače da se pripreme za prvu igru
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}