package com.mypro.particulate.main.model;

import org.springframework.stereotype.Component;

/*
 * 2025-01-06 파일 생성
 * 
 * 영업소별 주의보, 경보 상태 관리 클래스
 */
@Component
class AlertStatus {
    protected int countGrade1 = 0;
    protected int countGrade2 = 0;
    protected boolean grade1Alert = false;
    protected boolean grade2Alert = false;
    protected boolean grade1Release = false;
    protected boolean grade2Release = false;

    // 영업소 별 기준치를 넘는지 안 넘는지 확인
    public void update(Float value, AlertStandardModel alertStandardModel) {
        if (alertStandardModel.getDustType().toString().equals("Fine")) {
            pm10Count(value, alertStandardModel);
        } else if (alertStandardModel.getDustType().toString().equals("Ultra")) {
            pm25Count(value, alertStandardModel);
        }
    }

    private void pm10Count(Float value, AlertStandardModel alertStandardModel) {
        if (value >= alertStandardModel.getAlertGrade1()) {
            countGrade1++;
        } else {
            if (value < alertStandardModel.getReleaseGrade1() && countGrade1 >= alertStandardModel.getGrade1ThresholdTime()) {
                grade1Release = true;
            } else {
                countGrade1 = 0;
            }
        }
        
    }

    private void pm25Count(Float value, AlertStandardModel alertStandardModel) {
        if (value >= alertStandardModel.getAlertGrade2()) {
            countGrade2++;
        } else {
            if (value < alertStandardModel.getReleaseGrade2() && countGrade2 >= alertStandardModel.getGrade2ThresholdTime()) {
                grade2Release = true;
            } else {
                countGrade2 = 0;
            }
        }
    }

    // 주의보, 경보 알림 여부
    public String checkAlert(AlertStandardModel alertStandardModel) {
        if (countGrade1 >= alertStandardModel.getGrade1ThresholdTime()) {
            if (grade1Release) {
                grade1Release = false;
                grade1Alert = false;
                return "[알림] %s %s 영업소: %s 주의보 해제";
            } else if (grade1Alert) {
                return "[알림] %s %s 영업소: %s 주의보 진행중";
            } else {
                grade1Alert = true;
                return "[알림] %s %s 영업소: %s 주의보 발령";
            }
        }

        if (countGrade2 >= alertStandardModel.getGrade2ThresholdTime()) {
            if (grade2Release) {
                grade2Release = false;
                grade2Alert = false;
                return "[알림] %s %s 영업소: %s 경보 해제";
            } else if (grade2Alert) {
                return "[알림] %s %s 영업소: %s 경보 진행중";
            } else {
                grade2Alert = true;
                return "[알림] %s %s 영업소: %s 경보 발령";
            }
        }

        return null;
    }
}    
