package com.mypro.particulate.main.service;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.AlertModel;
import com.mypro.particulate.main.model.AlertStandardModel;
import com.mypro.particulate.main.model.AlertStatusPm10Tracker;
import com.mypro.particulate.main.model.AlertStatusPm25Tracker;
import com.mypro.particulate.main.model.DustType;
import com.mypro.particulate.main.repository.AlertRepository;

/* 
 * 2025-01-06 파일 생성
 * 
 * 경보, 주의보에 대한 발령 관련 서비스
 */
@Service
public class AlertService {

    private final AlertStatusPm10Tracker alertStatusPm10Tracker;
    private final AlertStatusPm25Tracker alertStatusPm25Tracker;
    private final AlertRepository alertRepository;

    public AlertService(AlertStatusPm10Tracker alertStatusPm10Tracker, AlertStatusPm25Tracker alertStatusPm25Tracker, AlertRepository alertRepository) {
        this.alertStatusPm10Tracker = alertStatusPm10Tracker;
        this.alertStatusPm25Tracker = alertStatusPm25Tracker;
        this.alertRepository = alertRepository;
    }

    public String process10AlertData(String[] messageList, AlertStandardModel AlertStandardModel) {
        float value = Float.parseFloat(messageList[2]);
        alertStatusPm10Tracker.update(value, AlertStandardModel);

        String message = alertStatusPm10Tracker.checkAlert(AlertStandardModel);
        if (message != null && !message.contains("해제")) {
            AlertModel alertModel = new AlertModel();
            alertModel.setDate(messageList[0]);
            alertModel.setDustType(DustType.Fine);
            alertModel.setGrade(message.split(" ")[5]);
            alertModel.setStation(messageList[1]);
            alertModel.setValue(value);

            alertRepository.save(alertModel);
        }
        return message;
    }

    public String process25AlertData(String[] messageList, AlertStandardModel AlertStandardModel) {
        float value = Float.parseFloat(messageList[3]);
        alertStatusPm25Tracker.update(value, AlertStandardModel);

        String message = alertStatusPm25Tracker.checkAlert(AlertStandardModel);
        if (message != null && !message.contains("해제")) {
            AlertModel alertModel = new AlertModel();
            alertModel.setDate(messageList[0]);
            alertModel.setDustType(DustType.Ultra);
            alertModel.setGrade(message.split(" ")[5]);
            alertModel.setStation(messageList[1]);
            alertModel.setValue(value);

            alertRepository.save(alertModel);
        }

        return message;
    }

    
}
