package com.mypro.particulate.main.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.mypro.particulate.main.service.DustService;

import ch.qos.logback.core.net.SyslogOutputStream;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DustService dustService;

    public ClientHandler(Socket socket, DustService dustService) {
        this.clientSocket = socket;
        this.dustService = dustService;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String alertInfo;
            while((alertInfo = in.readLine()) != null) {
                System.out.println("Received alert: " + alertInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
