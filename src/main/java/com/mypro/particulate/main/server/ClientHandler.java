package com.mypro.particulate.main.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import com.mypro.particulate.main.model.AlertStandardModel;
import com.mypro.particulate.main.model.DustType;
import com.mypro.particulate.main.model.GradeStandardModel;
import com.mypro.particulate.main.service.AlertService;
import com.mypro.particulate.main.service.DustGradeService;

/*
 * 2024-12-31 생성
 * 
 * 서버로 전송된 클라이언트의 메시지 처리
 */
public class ClientHandler implements Runnable {
    
    private final DustGradeService dustGradeService;
    private final AlertService alertService;
    
    private final Socket socket;
    private final GradeStandardModel pm10StandardModel;
    private final GradeStandardModel pm25StandardModel;
    private final AlertStandardModel pm10AlertModel;
    private final AlertStandardModel pm25AlertModel;

    public ClientHandler(DustGradeService dustGradeService, AlertService alertService, Socket socket, List<GradeStandardModel> standardModelList, List<AlertStandardModel> alertModelList) {
        this.dustGradeService = dustGradeService;
        this.alertService = alertService;
        this.socket = socket;
        this.pm10StandardModel = filterStandardModels(standardModelList, DustType.Fine);
        this.pm25StandardModel = filterStandardModels(standardModelList, DustType.Ultra);
        this.pm10AlertModel = filterAlertModels(alertModelList, DustType.Fine);
        this.pm25AlertModel = filterAlertModels(alertModelList, DustType.Ultra);
    }

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream()) {
                
            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) != -1) {
                String message = new String(buf, 0, bytesRead, "UTF-8");
                String[] messageList = message.split(", ");
    
                // PM10 처리
                String pm10Grade = dustGradeService.processDustData(messageList, output, pm10StandardModel);
                StringBuilder response = new StringBuilder();

                if (pm10Grade != null) {
                    response.append(pm10Grade).append("\n"); // PM10 상태 추가
                }

                String pm10Alert = alertService.process10AlertData(messageList, pm10AlertModel);
                if (pm10Alert != null) {
                    pm10Alert = String.format(pm10Alert, messageList[0], messageList[1], "미세먼지");
                    response.append(pm10Alert).append("\n"); // PM10 경고 추가
                }

                // PM2.5 처리
                String pm25Grade = dustGradeService.processDustData(messageList, output, pm25StandardModel);
                if (pm25Grade != null) {
                    response.append(pm25Grade).append("\n"); // PM2.5 상태 추가
                }

                String pm25Alert = alertService.process25AlertData(messageList, pm25AlertModel);
                if (pm25Alert != null) {
                    pm25Alert = String.format(pm25Alert, messageList[0], messageList[1], "초미세먼지");
                    response.append(pm25Alert).append("\n"); // PM2.5 경고 추가
                }

                // 응답 전송
                if (response.length() > 0) {
                    output.write(response.toString().getBytes("UTF-8"));
                    output.write("\n".getBytes("UTF-8"));
                    output.flush();
                }
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

    // dustType에 맞는 좋음, 보통, 나쁨, 매우나쁨 기준치로 넣어줌
    private GradeStandardModel filterStandardModels(List<GradeStandardModel> models, DustType type) {
        return models.stream() 
                .filter(model -> type.equals(model.getDustType()))
                .collect(Collectors.toList())
                .get(0);
    }

    // dustType에 맞는 주의보, 발령 기준치를 넣어줌
    private AlertStandardModel filterAlertModels(List<AlertStandardModel> models, DustType type) {
        return models.stream()
                .filter(model -> type.equals(model.getDustType()))
                .collect(Collectors.toList())
                .get(0);
    }
}
