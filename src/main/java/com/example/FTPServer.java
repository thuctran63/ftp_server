package com.example;

import java.net.ServerSocket;
import java.net.Socket;


public class FTPServer {
    private int port = 2023;
    public ServerSocket serverSocket = null;
    private static FTPServer instance;

    public static FTPServer getInstance() {
        if (instance == null) {
            instance = new FTPServer();
        }
        return instance;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port, port, null);
            System.out.println("Server FTP is running on port " + this.port + " ....");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ProcessClient(clientSocket)).start();
                System.out.println("New client connected:" + clientSocket.getInetAddress().getHostAddress());
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}