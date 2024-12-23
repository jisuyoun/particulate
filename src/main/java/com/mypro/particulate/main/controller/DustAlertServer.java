package com.mypro.particulate.main.controller;

import com.mypro.particulate.main.handler.ClientHandler;
import com.mypro.particulate.main.service.DustService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DustAlertServer {
    private DustService dustService;

    public DustAlertServer() {
        this.dustService = new DustService();
    }

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, dustService)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
