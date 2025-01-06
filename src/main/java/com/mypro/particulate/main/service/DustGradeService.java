package com.mypro.particulate.main.service;

import java.io.OutputStream;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.DustGradeModel;
import com.mypro.particulate.main.model.DustType;
import com.mypro.particulate.main.model.StandardModel;
import com.mypro.particulate.main.repository.DustGradeRepository;

/*
 * 2025-01-03 파일 생성
 * 
 * 보통 이상 경고에 대한 서비스
 */
@Service
public class DustGradeService {
    
    private final DustGradeRepository alertRepository;

    public DustGradeService (DustGradeRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    private static final String PM10_ALERT = "[알림] %s %s 영업소: 미세먼지 %s 상태";
    private static final String PM25_ALERT = "[알림] %s %s 영업소: 초미세먼지 %s 상태";

    // 클라이언트로부터 들어온 메시지 정리
    public String processDustData(String[] messageList, OutputStream output, StandardModel standardModel) {
        Float value = extractDustValue(messageList, standardModel);
        if (value == null) {
            return null;
        }

        return getAlertMessage(standardModel, value, messageList);
    }

    // 미세먼지 또는 초미세먼지 농도 추출
    public Float extractDustValue(String[] messageList, StandardModel standardModel) {
        try {
            if (standardModel.getDustType() == DustType.Fine) {
                return Float.parseFloat(messageList[2]);
            } else if (standardModel.getDustType() == DustType.Ultra) {
                return Float.parseFloat(messageList[3]);
            } else {
                System.out.println("[오류] 알 수 없는 DustType: " + standardModel.getDustType());
                return null;
            }
        } catch (Exception e) {
            System.out.println("[오류] 먼지 추출 중 오류 발생: " + e.getMessage());
            return null;
        }
    }

    // 클라이언트로 보낼 메시지 처리
    public String getAlertMessage(StandardModel standardModel, Float value, String[] messageList) {
        // 좋음 등급일 경우 제외
        if (value <= standardModel.getGrade1()) {
            return null;
        }

        String alertType = (standardModel.getDustType() == DustType.Fine) ? PM10_ALERT : PM25_ALERT;

        String status;
        String grade;
        if (value <= standardModel.getGrade2()) {
            status = "보통";
            grade = "1";
        } else if (value <= standardModel.getGrade3()) {
            status = "나쁨";
            grade = "2";
        } else if (value >= standardModel.getGrade4()) {
            status = "매우나쁨";
            grade = "3";
        } else {
            System.out.println("[오류] 알 수 없는 수치 발생: " + value);
            return null;
        }

        String message = String.format(alertType, messageList[0], messageList[1], status);
        insertAlertData(grade, messageList, standardModel.getDustType(), value);
    
        return message;
    }

    // 기준치 넘는 데이터 DB 저장
    public void insertAlertData(String grade, String[] messageList, DustType dustType, Float value) {
        DustGradeModel alertModel = new DustGradeModel();
        alertModel.setGrade(grade);
        alertModel.setDate(messageList[0]);
        alertModel.setStation(messageList[1]);
        alertModel.setDustType(dustType);
        alertModel.setValue(value);

        alertRepository.save(alertModel);
    }
}
