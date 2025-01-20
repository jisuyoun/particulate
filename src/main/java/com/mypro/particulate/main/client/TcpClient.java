package com.mypro.particulate.main.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 2024-12-23 파일 생성
 * 2025-01-02 서버 응답 읽기만 하는 것으로 변경
 * 
 * TCP 클라이언트
 */
public class TcpClient {

    private final static Logger log = LoggerFactory.getLogger(TcpClient.class);
    
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 8082;

        // csv 파일이 있는 경로
        String csvDirPath = "src/main/resources/csv";
        
        // csv 파일 목록 가져오기
        File csvDir = new File(csvDirPath);
        File[] csvFiles = csvDir.listFiles((dir, name) -> name.endsWith(".csv"));

        if (csvFiles != null && csvFiles.length > 0) {
            for (File csvFile : csvFiles) {
                try (Socket socket = new Socket(serverAddress, serverPort);
                    OutputStream output = socket.getOutputStream();
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {
        
                    log.info("전송할 CSV 파일명: {}", csvFile.getName());
                    DustDataSender dustDataSender = new DustDataSender(csvFile.getAbsolutePath(), output);
                    dustDataSender.sendData(input); // 미세먼지 데이터 전송
                } catch (IOException e) {
                    log.error("클라이언트 소켓 생성 중 오류 발생: {}", e.getMessage(), e);
                }
            }
        } else {
            log.error("CSV 파일이 디렉토리에 없습니다: {}", csvDir);
        }
    }
}