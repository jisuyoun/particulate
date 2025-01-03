package com.mypro.particulate.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/*
 * 2024-12-23 파일 생성
 * 2025-01-02 서버 응답 읽기만 하는 것으로 변경
 * 
 * TCP 클라이언트
 */
public class TcpClient {

    public static void main(String[] args) {

        String serverAddress = "localhost";
        int serverPort = 8082;
        
        try (Socket socket = new Socket(serverAddress, serverPort);
            OutputStream output = socket.getOutputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {

            String csvFilePath = "src\\main\\resources\\csv\\2023년3월_서울시_미세먼지.csv";
            DustDataSender dustDataSender = new DustDataSender(csvFilePath, output);
            dustDataSender.sendData(input); // 미세먼지 데이터 전송

            // 서버 응답 읽기
            String response;
            while ((response = input.readLine()) != null) {
                System.out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}