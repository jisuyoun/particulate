package com.mypro.particulate.main.service;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.AlertModel;
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

    public void insertAlertData(AlertModel alertModel) {
        alertRepository.save(alertModel);
    }


}
