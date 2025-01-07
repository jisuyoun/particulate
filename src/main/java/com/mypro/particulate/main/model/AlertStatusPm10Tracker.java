package com.mypro.particulate.main.model;

import org.springframework.stereotype.Component;

/*
 * 2025-01-06
 * 
 * 미세먼지 주의보, 경보 상태 관리 클래스
 */
@Component
public class AlertStatusPm10Tracker extends AlertStatus {
    @Override
    public void update(Float value, AlertStandardModel AlertStandardModel) {
        super.update(value, AlertStandardModel);
    }

    @Override
    public String checkAlert(AlertStandardModel AlertStandardModel) {
        return super.checkAlert(AlertStandardModel);
    }
}
