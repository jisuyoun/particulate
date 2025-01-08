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
    public void update(String mapKey, float value, AlertStandardModel alertStandardModel) {
        super.update(mapKey, value, alertStandardModel);
    }

    @Override
    public String checkAlert(String mapKey,AlertStandardModel alertStandardModel) {
        return super.checkAlert(mapKey, alertStandardModel);
    }
}
