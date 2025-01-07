package com.mypro.particulate.main.service;

import java.io.OutputStream;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.DustGradeModel;
import com.mypro.particulate.main.model.DustType;
import com.mypro.particulate.main.model.GradeStandardModel;
import com.mypro.particulate.main.repository.DustGradeRepository;

/*
 * 2025-01-03 파일 생성
 * 
 * 보통 이상 경고에 대한 서비스
 */
@Service
public class DustGradeService {
    
    private final DustGradeRepository dustGradeRepository;

    public DustGradeService (DustGradeRepository dustGradeRepository) {
        this.dustGradeRepository = dustGradeRepository;
    }

    private static final String PM10_ALERT = "[알림] %s %s 영업소: 미세먼지 %s 상태";
    private static final String PM25_ALERT = "[알림] %s %s 영업소: 초미세먼지 %s 상태";

    // 클라이언트로부터 들어온 메시지 정리
    public String processDustData(String[] messageList, OutputStream output, GradeStandardModel gradeStandardModel) {
        Float value = extractDustValue(messageList, gradeStandardModel);
        if (value == null) {
            return null;
        }

        return getAlertMessage(gradeStandardModel, value, messageList);
    }

    // 미세먼지 또는 초미세먼지 농도 추출
    public Float extractDustValue(String[] messageList, GradeStandardModel gradeStandardModel) {
        try {
            if (gradeStandardModel.getDustType() == DustType.Fine) {
                return Float.parseFloat(messageList[2]);
            } else if (gradeStandardModel.getDustType() == DustType.Ultra) {
                return Float.parseFloat(messageList[3]);
            } else {
                System.out.println("[오류] 알 수 없는 DustType: " + gradeStandardModel.getDustType());
                return null;
            }
        } catch (Exception e) {
            System.out.println("[오류] 먼지 추출 중 오류 발생: " + e.getMessage());
            return null;
        }
    }

    // 클라이언트로 보낼 메시지 처리
    public String getAlertMessage(GradeStandardModel gradeStandardModel, Float value, String[] messageList) {
        // 좋음 등급일 경우 제외
        if (value <= gradeStandardModel.getGrade1()) {
            return null;
        }

        String alertType = (gradeStandardModel.getDustType() == DustType.Fine) ? PM10_ALERT : PM25_ALERT;

        String status;
        String grade;
        if (value <= gradeStandardModel.getGrade2()) {
            status = "보통";
            grade = "1";
        } else if (value <= gradeStandardModel.getGrade3()) {
            status = "나쁨";
            grade = "2";
        } else if (value >= gradeStandardModel.getGrade4()) {
            status = "매우나쁨";
            grade = "3";
        } else {
            System.out.println("[오류] 알 수 없는 수치 발생: " + value);
            return null;
        }

        String message = String.format(alertType, messageList[0], messageList[1], status);
        insertAlertData(grade, messageList, gradeStandardModel.getDustType(), value);
    
        return message;
    }

    // 기준치 넘는 데이터 DB 저장
    public void insertAlertData(String grade, String[] messageList, DustType dustType, Float value) {
        DustGradeModel dustGradeModel = new DustGradeModel();
        dustGradeModel.setGrade(grade);
        dustGradeModel.setDate(messageList[0]);
        dustGradeModel.setStation(messageList[1]);
        dustGradeModel.setDustType(dustType);
        dustGradeModel.setValue(value);

        dustGradeRepository.save(dustGradeModel);
    }
}
