package com.mypro.particulate.main.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/*
 * 2025-01-06 파일 생성
 * 
 * 영업소별 주의보, 경보 상태 관리 클래스
 */
@Component
class AlertStatus {
    private Map<String, AlertStatusModel> alertStatusMap;
    public AlertStatus() {
        this.alertStatusMap = new HashMap<>();
    }

    // 영업소 별 기준치를 넘는지 안 넘는지 확인
    public void update(String mapKey, float value, AlertStandardModel alertStandardModel) {
        if (!this.alertStatusMap.containsKey(mapKey)) {
            AlertStatusModel alertStatusModel = new AlertStatusModel();
            this.alertStatusMap.put(mapKey, alertStatusModel);
        }

        boolean pm10YN = alertStandardModel.getDustType().toString().equals("Fine") ? true : false;

        processCount(mapKey, value, alertStandardModel, pm10YN);

    }

    // 기준치 카운트 처리
    private void processCount(String mapKey, float value, AlertStandardModel alertStandardModel, boolean pm10YN) {
        AlertStatusModel alertStatusModel = this.alertStatusMap.get(mapKey);
        float alertGrade1 = alertStandardModel.getAlertGrade1();  // 주의보 발령 기준
        float alertGrade2= alertStandardModel.getAlertGrade2();   // 경보 발령 기준
        int grade1ThresholdTime = alertStandardModel.getGrade1ThresholdTime(); // 주의보 발령 기준 시간
        int grade2ThresholdTime = alertStandardModel.getGrade2ThresholdTime(); // 경보 발령 기준 시간
        float releaseGrade1 = alertStandardModel.getReleaseGrade1();  // 주의보 해제 기준
        float releaseGrade2 = alertStandardModel.getReleaseGrade2();  // 경보 해제 기준
        
        // 주의보 및 경보 해제 처리
        checkRelease(value, alertStatusModel, releaseGrade1, releaseGrade2, grade1ThresholdTime, grade2ThresholdTime);

        // 카운트 증가 처리
        incrementCount(value, alertStatusModel, alertGrade1, alertGrade2);

        this.alertStatusMap.put(mapKey, alertStatusModel);
    }

    // 주의보 및 경보 해제 체크
    private void checkRelease(float value, AlertStatusModel alertStatusModel, float releaseGrade1, float releaseGrade2, int grade1ThresholdTime, int grade2ThresholdTime) {
        if (value < releaseGrade1 && alertStatusModel.grade1Alert) {
            alertStatusModel.grade1Release = true;
            alertStatusModel.countGrade1 = 0;
        }
        if (value < releaseGrade2 && alertStatusModel.grade2Alert) {
            alertStatusModel.grade2Release = true;
            alertStatusModel.countGrade2 = 0;
        }
    }

    // 카운트 증가 처리
    private void incrementCount(float value, AlertStatusModel alertStatusModel, float alertGrade1, float alertGrade2) {
        if (value >= alertGrade1) {
            alertStatusModel.countGrade1++;
            if (value >= alertGrade2) {
                alertStatusModel.countGrade2++;
            }
        }

        // 기준 미만일 경우 카운트 초기화
        if (value < alertGrade2 && !alertStatusModel.grade2Alert) {
            alertStatusModel.countGrade2 = 0;
            if (value < alertGrade1 && !alertStatusModel.grade1Alert) {
                alertStatusModel.countGrade1 = 0;
            }
        }
    }

    // 주의보, 경보 알림 여부
    public String checkAlert(String mapKey, AlertStandardModel alertStandardModel) {
        AlertStatusModel alertStatusModel = this.alertStatusMap.get(mapKey);
        
        // 경보 해제 알림
        if (alertStatusModel.grade2Release) {
            alertStatusModel.grade2Release = false;
            alertStatusModel.grade2Alert = false;
            this.alertStatusMap.put(mapKey, alertStatusModel);
            
            return "[알림] %s %s 영업소: %s 경보 해제";
        }
        
        // 주의보 해제 알림
        if (alertStatusModel.grade1Release) {
            alertStatusModel.grade1Release = false;
            alertStatusModel.grade1Alert = false;
            this.alertStatusMap.put(mapKey, alertStatusModel);

            return "[알림] %s %s 영업소: %s 주의보 해제";
        }
        
        // 경보 알림
        if (alertStatusModel.countGrade2 >= alertStandardModel.getGrade2ThresholdTime()) {
            if (alertStatusModel.grade2Alert) {
                return "[알림] %s %s 영업소: %s 경보 진행중";
            } else {
                alertStatusModel.grade2Alert = true;
                this.alertStatusMap.put(mapKey, alertStatusModel);

                return "[알림] %s %s 영업소: %s 경보 발령";
            }
        }

        // 주의보 알림
        if (alertStatusModel.countGrade1 >= alertStandardModel.getGrade1ThresholdTime()) {
            if (alertStatusModel.grade1Alert) {
                return "[알림] %s %s 영업소: %s 주의보 진행중";
            } else {
                alertStatusModel.grade1Alert = true;
                this.alertStatusMap.put(mapKey, alertStatusModel);

                return "[알림] %s %s 영업소: %s 주의보 발령";
            }
        }

        return null;
    }
}    

class AlertStatusModel {
    protected int countGrade1 = 0;
    protected int countGrade2 = 0;
    protected boolean grade1Alert = false;
    protected boolean grade2Alert = false;
    protected boolean grade1Release = false;
    protected boolean grade2Release = false;
}
