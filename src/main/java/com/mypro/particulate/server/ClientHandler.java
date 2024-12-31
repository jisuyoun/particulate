package com.mypro.particulate.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import com.mypro.particulate.main.model.StandardModel;

/*
 * 2024-12-31 생성
 * 클라이언트의 메시지 처리
 */
public class ClientHandler implements Runnable {
    
    private final Socket socket;
    private final List<StandardModel> pm10ModelList;
    private final List<StandardModel> pm25ModelList;

    public ClientHandler(Socket socket, List<StandardModel> standardModelList) {
        this.socket = socket;
        this.pm10ModelList = standardModelList.stream()
                            .filter(model -> "10".equals(model.getDustType()))
                            .collect(Collectors.toList());
        this.pm25ModelList = standardModelList.stream()
                            .filter(model -> "2.5".equals(model.getDustType()))
                            .collect(Collectors.toList());
    }

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream()) {
                
            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) != -1) {
                String message = new String(buf, 0, bytesRead, "UTF-8");
                handleClientMessage(message, output);
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

    private void handleClientMessage(String message, OutputStream output) throws IOException {
        String[] messageList = message.split(", ");
        int pm10Value = Integer.parseInt(messageList[2]);
        int pm25Value = Integer.parseInt(messageList[3]);

        // pm10 처리
        processPm10(pm10Value, output);
        // pm2.5 처리
        processPm25(pm25Value, output);
    }

    private void processPm10 (int pm10Value, OutputStream output) throws IOException {
        if (pm10ModelList.get(0).getGrade1() < pm10Value 
                    && pm10Value <= pm10ModelList.get(0).getGrade2()) {
            output.write("Alert: 미세먼지 보통 상태".getBytes("UTF-8"));
            output.flush();
        } else if (pm10Value <= pm10ModelList.get(0).getGrade3()) {
            output.write("Alert: 미세먼지 나쁨 상태".getBytes("UTF-8"));
            output.flush();
        } else if (pm10Value >= pm10ModelList.get(0).getGrade4()) {
            output.write("Alert: 미세먼지 매우 나쁨 상태".getBytes("UTF-8"));
            output.flush();
        }
    }

    private void processPm25 (int pm25Value, OutputStream output) throws IOException {
        if (pm25ModelList.get(0).getGrade1() < pm25Value 
                && pm25Value <= pm25ModelList.get(0).getGrade2()) {
            output.write("Alert: 초미세먼지 보통 상태".getBytes("UTF-8"));
            output.flush();
        } else if (pm25Value <= pm25ModelList.get(0).getGrade3()) {
            output.write("Alert: 초미세먼지 나쁨 상태".getBytes("UTF-8"));
            output.flush();
        } else if (pm25Value >= pm25ModelList.get(0).getGrade4()) {
            output.write("Alert: 초미세먼지 매우 나쁨 상태".getBytes("UTF-8"));
            output.flush();
        }
    }
}
