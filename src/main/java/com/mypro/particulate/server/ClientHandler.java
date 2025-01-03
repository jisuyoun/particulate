package com.mypro.particulate.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import com.mypro.particulate.main.model.DustType;
import com.mypro.particulate.main.model.StandardModel;
import com.mypro.particulate.main.service.AlertService;
import com.mypro.particulate.main.service.StandardService;

/*
 * 2024-12-31 생성
 * 
 * 서버로 전송된 클라이언트의 메시지 처리
 */
public class ClientHandler implements Runnable {
    
    private final AlertService alertService;
    private final StandardService standardService;
    private final Socket socket;
    private final StandardModel pm10Model;
    private final StandardModel pm25Model;;

    public ClientHandler(AlertService alertService, StandardService standardService, Socket socket, List<StandardModel> standardModelList) {
        this.alertService = alertService;
        this.standardService = standardService;
        this.socket = socket;
        this.pm10Model = filterModels(standardModelList, DustType.Fine);
        this.pm25Model = filterModels(standardModelList, DustType.Ultra);
    }

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream()) {
                
            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) != -1) {
                String message = new String(buf, 0, bytesRead, "UTF-8");
                
                processDustDate(message, output);
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

    // dustType에 맞는 기준치로 넣어줌
    private StandardModel filterModels(List<StandardModel> models, DustType type) {
        return models.stream() 
                .filter(model -> type.equals(model.getDustType()))
                .collect(Collectors.toList())
                .get(0);
    }

    // 클라이언트로 전송할 메시지 처리
    private void processDustDate (String message, OutputStream output) throws IOException {        
        String[] messageList = message.split(", ");
        Float pm10Value = Float.parseFloat(messageList[2]);
        Float pm25Value = Float.parseFloat(messageList[3]);

        String pm10Alert = alertService.checkDustGrade(pm10Model, "10", pm10Value);
        String pm25Alert = alertService.checkDustGrade(pm25Model, "2.5", pm25Value);

        if (pm10Alert != null) {
            alertService.insertAlertData(pm10Alert, messageList, pm10Value, DustType.Fine);
            
            output.write((pm10Alert + "\n").getBytes("UTF-8"));
            output.flush();
        }
        if (pm25Alert != null) {
            alertService.insertAlertData(pm25Alert, messageList, pm25Value, DustType.Ultra);

            output.write((pm25Alert + "\n").getBytes("UTF-8"));
            output.flush();
        }
    }
}
