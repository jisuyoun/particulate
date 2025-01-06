package com.mypro.particulate.main.model;

import org.springframework.stereotype.Component;

@Component
public class AlertStatusPm25Tracker extends AlertStatus {
 
    @Override
    public void update(Float value, AlertModel alertModel) {
        super.update(value, alertModel);
    }

    @Override
    public String checkAlert(AlertModel alertModel) {
        return super.checkAlert(alertModel);
    }
}
