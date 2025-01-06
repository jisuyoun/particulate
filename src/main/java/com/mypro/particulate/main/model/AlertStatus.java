package com.mypro.particulate.main.model;

import org.springframework.stereotype.Component;

/*
 * 2025-01-06 파일 생성
 * 
 * 영업소별 주의보, 경보 상태
 */
@Component
class AlertStatus {
    protected int countGrade1 = 0;
    protected int countGrade2 = 0;
    protected boolean grade1Release = false;
    protected boolean grade2Release = false;

    // 영업소 별 기준치를 넘는지 안 넘는지 확인
    public void update(Float value, AlertModel alertModel) {
        if (value >= alertModel.getAlertGrade1()) {
            countGrade1++;
        } else {
            if (value < alertModel.getReleaseGrade1() && countGrade1 >= alertModel.getReleaseGrade1()) {
                grade1Release = true;
            }
            countGrade1 = 0;
        }

        if (value >= alertModel.getAlertGrade2()) {
            countGrade2++;
        } else {
            if (value < alertModel.getReleaseGrade2() && countGrade2 >= alertModel.getReleaseGrade2()) {
                grade2Release = true;
            }
            countGrade2 = 0;
        }
    }

    // 주의보, 경보 알림 여부
    public String checkAlert(AlertModel alertModel) {
        if (grade1Release) {
            countGrade1 = 0;
            grade1Release = false;
            return "[알림] %s %s 영업소: %s 주의보 해제";
        }

        if (grade2Release) {
            countGrade2 = 0;
            grade2Release = false;
            return "[알림] %s %s 영업소: %s 경보 해제";
        }

        if (countGrade1 >= alertModel.getGrade1ThresholdTime()) {
            return "[알림] %s %s 영업소: %s 주의보 발령";
        }
        if (countGrade2 >= alertModel.getGrade2ThresholdTime()) {
            return "[알림] %s %s 영업소: %s 경보 발령";
        }
        
        return null;
    }
}    
