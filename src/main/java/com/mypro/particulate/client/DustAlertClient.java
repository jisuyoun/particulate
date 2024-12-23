package com.mypro.particulate.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.mypro.particulate.main.model.DustModel;
import com.mypro.particulate.main.service.DustService;

public class DustAlertClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        String csvFilePath = "src\\main\\resources\\csv\\2023년3월_서울시_미세먼지.csv";
        DustService dustService = new DustService();

        try {
            List<DustModel> dustModelList = dustService.readDustData(csvFilePath);
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    
                    for (DustModel dustModel : dustModelList) {
                        if (dustService.isDustAboveThreshold(dustModel)) {
                            String alertInfo = String.format("Alert: %s, Station: %s, Fine Dust: %d", 
                                dustModel.getDate(), dustModel.getStation(), dustModel.getFineDust());
                                out.println(alertInfo);
                            }
                    }

                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
