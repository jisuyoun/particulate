package com.mypro.particulate.main.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.universalchardet.UniversalDetector;

import com.mypro.particulate.main.model.DustModel;

/*
 * 2025-01-02 파일 생성
 * 
 * 미세먼지 및 초미세먼지 데이터를 CSV에서 읽어 서버로 전송
 */
public class DustDataSender {
    private final List<DustModel> dustModelList;
    private final OutputStream output;

    public DustDataSender (String csvFilePath, OutputStream output) throws IOException {
        this.output = output;
        this.dustModelList = readDustData(csvFilePath);
    }

    // csv 파일 읽기
    private List<DustModel> readDustData (String csvFilePath) throws IOException {
        List<DustModel> dustModelList = new ArrayList<>();
        String encoding = detectFileEncoding(csvFilePath);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilePath), encoding))) {
            br.readLine(); // 헤더 건너뛰기
            String line;
            while((line = br.readLine()) != null) {
                String[] lineList = line.split(",");
                String[] dateStr = lineList[0].split(" ")[0].split("-");
                String date = dateStr[0] + dateStr[1] + dateStr[2] + lineList[0].split(" ")[1];
                DustModel dustModel = new DustModel();
                dustModel.setDate(date);
                dustModel.setStation(lineList[1]);

                // 미세먼지 농도 설정
                dustModel.setFineDust((lineList.length > 3 && !lineList[3].trim().isEmpty())
                                        ? Integer.parseInt(lineList[3]) : 0);
                
                // 초미세먼지 농도 설정
                dustModel.setUltraFineDust(lineList.length > 4 && !lineList[4].trim().isEmpty()
                                            ? Integer.parseInt(lineList[4]) : 0);

                dustModelList.add(dustModel);
            }
        }

        return dustModelList;
    }

    // 메시지 전송
    public void sendData(BufferedReader input) throws IOException {
        try {
            for (DustModel dustModel : this.dustModelList) {
                String message = String.format("%s, %s, %d, %d",
                            dustModel.getDate(), dustModel.getStation(), dustModel.getFineDust(), dustModel.getUltraFineDust());
                output.write((message).getBytes("UTF-8")); // 메시지 전송
                output.flush(); // 버퍼 비우고 전송

                // 서버 응답 즉시 읽기
                String response = input.readLine();
                if (response != null) {
                    System.out.println("서버 응답: " + response);
                }

                Thread.sleep(100); // 스레드 대기를 안 할 경우 데이터가 한 줄로 뭉쳐서 전송이 됨
            }
        } catch (InterruptedException e) {  // throws로 IOException과 함께 보낼 경우 TcpClient에서는 발생할 가능성이 없는 오류로 오류가 남
            Thread.currentThread().interrupt(); // 스레드의 인터럽트 상태를 복원
            throw new IOException("데이터 전송 중 인터럽트 발생", e);
        }
    }
        
    // csv 인코딩 감지
    private String detectFileEncoding(String csvFilePath) throws IOException {
        
        try (FileInputStream fis = new FileInputStream(csvFilePath)) {
            byte[] buf = new byte[4096];
            UniversalDetector detector = new UniversalDetector(null);
            
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();

            String detectCharset = detector.getDetectedCharset();
            if (detectCharset != null && Charset.isSupported(detectCharset)) {
                return detectCharset;
            }
        } 
        
        return "UTF-8";
    }
}
