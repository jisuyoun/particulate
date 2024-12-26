package com.mypro.particulate.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.stereotype.Component;

/*
 * 2024-12-23 파일 생성
 * TCP 서버
 */
public class TcpServer {

    public static void main(String[] args) {
        int port = 8082;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("서버가 포트 " + port + "에서 대기 중 입니다.");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("새 클라이언트가 연결되었습니다.");
                handleClient(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 클라이언트 메시지 읽기
    private static void handleClient(Socket socket) {
        try (InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream()) {
            
            byte[] buf = new byte[1024];
            int bytesRead;

            // 클라이언트로부터 메시지를 반복적으로 읽음
            while ((bytesRead = input.read(buf)) != -1) {
                String message = new String(buf, 0, bytesRead, "UTF-8");
                System.out.println("받은 메시지: " + message);
                output.write(("Echo: " + message).getBytes("UTF-8"));
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}
