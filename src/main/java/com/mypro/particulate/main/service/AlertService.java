package com.mypro.particulate.main.service;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.AlertModel;
import com.mypro.particulate.main.model.DustType;
import com.mypro.particulate.main.model.StandardModel;
import com.mypro.particulate.main.repository.AlertRepository;

/*
 * 2025-01-03 파일 생성
 * 
 * 보통 이상 경고에 대한 서비스
 */
@Service
public class AlertService {
    
    private final AlertRepository alertRepository;

    public AlertService (AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    private static final String PM10_ALERT = "미세먼지 %s 상태";
    private static final String PM25_ALERT = "초미세먼지 %s 상태";

        // 클라이언트로 보낼 메시지 처리
    public String checkDustGrade(StandardModel standardModel, String type, Float value) {
        // 좋음 등급일 경우 제외
        if (value <= standardModel.getGrade1()) {
            return null;
        }

        String alertType = "";
        switch (type) {
            case "10":
                alertType = PM10_ALERT;
                break;
            case "2.5":
                alertType = PM25_ALERT;
                break;
        }

        String message = null;
        if (value <= standardModel.getGrade2()) {
            message = String.format(alertType, "보통_1");
            // 여기서 저장하는 것으로 변경하기
        } else if (value <= standardModel.getGrade3()) {
            message = String.format(alertType, "나쁨_2");
        } else if (value >= standardModel.getGrade4()) {
            message = String.format(alertType, "매우나쁨_3");
        }
        

        return message;
    }

    // 보통 이상 등급인 데이터 저장
    public void insertAlertData(String alert, String[] messageList, Float value, DustType dustType) {
        AlertModel alertModel = new AlertModel();
        alertModel.setGrade(getGrade(value, alert));
        alertModel.setDate(messageList[0]);
        alertModel.setStation(messageList[1]);
        alertModel.setDustType(dustType);
        alertModel.setValue(value);

        alertRepository.save(alertModel);
    }

    // 등급 구분
    private String getGrade(Float value, String alert) {
        if (alert.contains("보통")) {
            return "1";
        } else if (alert.contains("매우나쁨")) {
            return "3";
        } else if (alert.contains("나쁨")) {
            return "2";
        }

        return null;
    }
}
