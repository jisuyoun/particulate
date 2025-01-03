package com.mypro.particulate.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import com.mypro.particulate.main.model.AlertModel;
import com.mypro.particulate.main.model.DustType;
import com.mypro.particulate.main.model.StandardModel;
import com.mypro.particulate.main.service.AlertService;

/*
 * 2024-12-31 생성
 * 
 * 서버로 전송된 클라이언트의 메시지 처리
 */
public class ClientHandler implements Runnable {
    
    private final AlertService alertService;
    private final Socket socket;
    private final List<StandardModel> pm10ModelList;
    private final List<StandardModel> pm25ModelList;

    private static final String PM10_ALERT = "미세먼지 %s 상태";
    private static final String PM25_ALERT = "초미세먼지 %s 상태";

    public ClientHandler(AlertService alertService, Socket socket, List<StandardModel> standardModelList) {
        this.alertService = alertService;
        this.socket = socket;
        this.pm10ModelList = filterModels(standardModelList, DustType.Fine);
        this.pm25ModelList = filterModels(standardModelList, DustType.Ultra);
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
    private List<StandardModel> filterModels(List<StandardModel> models, DustType type) {
        return models.stream() 
                .filter(model -> type.equals(model.getDustType()))
                .collect(Collectors.toList());
    }

    private void processDustDate (String message, OutputStream output) throws IOException {
        
        String[] messageList = message.split(", ");
        Float pm10Value = Float.parseFloat(messageList[2]);
        Float pm25Value = Float.parseFloat(messageList[3]);

        handleDustType("1", output, pm10Value, pm10ModelList, DustType.Fine, messageList);
         handleDustType("2", output, pm25Value, pm10ModelList, DustType.Ultra, messageList);
    }

    private void handleDustType(String type, OutputStream output, Float value, List<StandardModel> standardModelList, DustType dustType, String[] messageList) throws IOException {
        AlertModel alertModel = new AlertModel();
        String alertMessage = getAlertMessage(type, value, standardModelList);

        if (alertMessage != null) {
            // 클라이언트로 메시지 전송
            output.write((alertMessage + "\n").getBytes("UTF-8"));
            output.flush();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // DB에 경고 저장
            alertModel.setGrade(getGrade(value, standardModelList));
            alertModel.setDate(messageList[0]);
            alertModel.setStation(messageList[1]);
            alertModel.setDustType(dustType);
            alertModel.setValue(value);

            alertService.insertAlertData(alertModel);
        }
    }

    private String getAlertMessage(String type, Float value, List<StandardModel> standardModelList) {
        String alertType = "";
        switch (type) {
            case "1":
                alertType = PM10_ALERT;
                break;
            case "2":
                alertType = PM25_ALERT;
                break;
        }

        if (standardModelList.get(0).getGrade1() < value
            && value <= standardModelList.get(0).getGrade2()) {
                return String.format(alertType, "보통");
        } else if (value <= standardModelList.get(0).getGrade3()) {
            return String.format(alertType, "나쁨");
        } else if (value >= standardModelList.get(0).getGrade4()) {
            return String.format(alertType, "매우나쁨");
        }

        return null;
    }

    private String getGrade(Float value, List<StandardModel> standardModelList) {
        if (standardModelList.get(0).getGrade1() < value
            && value <= standardModelList.get(0).getGrade2()) {
                return "1";
        } else if (value <= standardModelList.get(0).getGrade3()) {
            return "2";
        } else if (value >= standardModelList.get(0).getGrade4()) {
            return "3";
        }

        return null;
    }

}
