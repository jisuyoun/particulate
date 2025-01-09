package com.mypro.particulate.main.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mypro.particulate.main.model.AlertStandardModel;
import com.mypro.particulate.main.model.GradeStandardModel;
import com.mypro.particulate.main.service.AlertService;
import com.mypro.particulate.main.service.DustGradeService;
import com.mypro.particulate.main.service.StandardService;

/*
 * 2024-12-23 파일 생성
 * 2024-12-26 Component로 변경
 * 2024-12-31 서버 소켓을 열고, 클라이언트 연결 처리만 하는 것으로 변경
 * 
 * Tcp 서버
 */
@Component
public class TcpServer {

    private final DustGradeService dustGradeService;
    private final AlertService alertService;

    private final List<GradeStandardModel> standardModelList;
    private final List<AlertStandardModel> alertModelList;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public TcpServer(DustGradeService dustGradeService, AlertService alertSerivce, StandardService standardService) {
        this.dustGradeService = dustGradeService;
        this.alertService = alertSerivce;

        try {
            // 기준치 가져오기, 테이블이 없을 경우 테이블 생성
            this.standardModelList = standardService.getDustStandard();
            // 경보, 주의보 기준치 가져오기, 테이블이 없을 경우 테이블 생성
            this.alertModelList = standardService.getDustAlertStandard();
        } catch (Exception e) {
            throw new RuntimeException("기준치를 가져오는 중 오류 발생", e);
        }
        
        startService();
    }

    public void startService() {
        new Thread(() -> {  // 비동기 처리
            try (ServerSocket serverSocket = new ServerSocket(8082)) {
                System.out.println("서버가 포트 8082에서 대기 중입니다.");
                log.info("서버가 포트 8082에서 대기 중입니다.");
                while (true) {
                    Socket socket = serverSocket.accept();
                    log.info("새 클라이언트가 연결되었습니다.");

                    new Thread(new ClientHandler(dustGradeService, alertService, socket, standardModelList, alertModelList)).start();
                }
            } catch (IOException e) {
                log.error("서버 소켓 생성 중 오류 발생: {}", e.getMessage(), e);
            }
        }).start();
    }
}