package com.mypro.particulate.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.universalchardet.UniversalDetector;

import com.mypro.particulate.main.model.DustModel;

/*
 * 2024-12-23 파일 생성
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
            List<DustModel> dustModelList = readDustData(csvFilePath);
            
            for (DustModel dustModel : dustModelList) {
                String message = String.format("%s, %s, %d, %d", 
                                dustModel.getDate(), dustModel.getStation(), dustModel.getFineDust(), dustModel.getUltraFineDust());
                output.write(message.getBytes("UTF-8")); // 메시지 전송

                Thread.sleep(100); // 대기 시간이 없을 경우 tcp server에서 한 줄 씩 처리를 못함

                // 서버 응답 읽기
                String response = input.readLine();
                System.out.println("서버 응답: " + response);

                Thread.sleep(100);
            }

            output.flush();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // csv 파일 읽기
    private static List<DustModel> readDustData(String csvFilePath) throws IOException {
        List<DustModel> dustModelList = new ArrayList<>();
        
        String encoding = detectFileEncoding(csvFilePath);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilePath), encoding))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] lineList = line.split(",");
                
                DustModel dustModel = new DustModel();
                dustModel.setDate(lineList[0].split(" ")[0]);
                dustModel.setTime(lineList[0].split(" ")[1]);
                dustModel.setStation(lineList[1]);

                // 미세먼지 농도 설정
                if (lineList.length > 3 && !lineList[3].trim().isEmpty()) {
                    dustModel.setFineDust((Integer.parseInt(lineList[3])));
                } else {
                    dustModel.setFineDust(0);
                }

                // 초미세먼지 농도 설정
                if (lineList.length > 4 && !lineList[4].trim().isEmpty()) {
                    dustModel.setUltraFineDust(Integer.parseInt(lineList[4]));
                } else {
                    dustModel.setUltraFineDust(0);
                }

                dustModelList.add(dustModel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dustModelList;
    }

    // 파일 인코딩 감지
    private static String detectFileEncoding(String csvFilePath) throws IOException {
        FileInputStream fis = null;
        String detectedCharset = "";

        try {
            byte[] buf = new byte[4096];
            fis = new FileInputStream(csvFilePath);

            UniversalDetector detector = new UniversalDetector(null);

            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();

            detectedCharset = detector.getDetectedCharset();
            if (detectedCharset != null && detector.isDone() && Charset.isSupported(detectedCharset)) {
                detector.reset();
                return detectedCharset;
            }
        } catch (Exception e) {
            return "";
        } finally {
            try {
                if (fis != null)
                    fis.close();
                } catch (Exception e) {}
        }
        return detectedCharset;
    }

}