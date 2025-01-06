package com.mypro.particulate.main.service;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.AlertModel;
import com.mypro.particulate.main.model.AlertStatusPm10Tracker;
import com.mypro.particulate.main.model.AlertStatusPm25Tracker;

/* 
 * 2025-01-06 파일 생성
 * 
 * 경보, 주의보에 대한 발령 관련 서비스
 */
@Service
public class AlertService {

    private final AlertStatusPm10Tracker alertStatusPm10Tracker;
    private final AlertStatusPm25Tracker alertStatusPm25Tracker;

    public AlertService(AlertStatusPm10Tracker alertStatusPm10Tracker, AlertStatusPm25Tracker alertStatusPm25Tracker) {
        this.alertStatusPm10Tracker = alertStatusPm10Tracker;
        this.alertStatusPm25Tracker = alertStatusPm25Tracker;
    }

    public String process10AlertData(String[] messageList, AlertModel alertModel) {
        float value = Float.parseFloat(messageList[2]);
        alertStatusPm10Tracker.update(value, alertModel);

        String alert = alertStatusPm10Tracker.checkAlert(alertModel);

        return alert;
    }

    public String pricess25AlertData(String[] messageList, AlertModel alertModel) {
        float value = Float.parseFloat(messageList[3]);
        alertStatusPm25Tracker.update(value, alertModel);

        String alert = alertStatusPm25Tracker.checkAlert(alertModel);

        return alert;
    }

    
}
