package com.mypro.particulate.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mypro.particulate.main.model.StandardModel;
import com.mypro.particulate.main.service.DustService;

/*
 * 2024-12-23 파일 생성
 * TCP 서버
 * 2024-12-26 
 * Component로 변경
 * 2024-12-31
 * 서버 소켓을 열고, 클라이언트 연결 처리로 변경
 */
@Component
public class TcpServer {

    private final DustService dustService;

    public TcpServer(DustService dustService) {
        this.dustService = dustService;
        startService();
    }

    public void startService() {
        new Thread(() -> {  // 비동기 처리
            try (ServerSocket serverSocket = new ServerSocket(8082)) {
                System.out.println("서버가 포트 8082에서 대기 중입니다.");
                List<StandardModel> standardModelList = dustService.getDustStandard();
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("새 클라이언트가 연결되었습니다.");
                    new Thread(new ClientHandler(socket, standardModelList)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
