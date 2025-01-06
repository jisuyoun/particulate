package com.mypro.particulate.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.AlertModel;
import com.mypro.particulate.main.model.DustType;
import com.mypro.particulate.main.model.StandardModel;
import com.mypro.particulate.main.repository.AlertRepository;
import com.mypro.particulate.main.repository.StandardRepository;

/*
 * 2024-12-23 파일 생성
 * 
 * 미세먼지 기준치 관련 서비스
 */
@Service
public class StandardService {

    private final StandardRepository standardRepository;
    private final AlertRepository alertRepository;

    public StandardService (StandardRepository standardRepository, AlertRepository alertRepository) {
        this.standardRepository = standardRepository;
        this.alertRepository = alertRepository;
    }

    // 좋음, 보통, 나쁨, 매우나쁨 초기 데이터 생성
    private void initializeStandardTable () {
        // PM10 데이터 삽입
        StandardModel standardModel = new StandardModel();
        standardModel.setDustType(DustType.Fine);
        standardModel.setGrade1(30);
        standardModel.setGrade2(81);
        standardModel.setGrade3(150);
        standardModel.setGrade4(151);

        standardRepository.save(standardModel);

        // PM2.5 데이터 삽입
        standardModel = new StandardModel();
        standardModel.setDustType(DustType.Ultra);
        standardModel.setGrade1(15);
        standardModel.setGrade2(35);
        standardModel.setGrade3(75);
        standardModel.setGrade4(76);

        standardRepository.save(standardModel);
   }

    // 좋음, 보통, 나쁨, 매우나쁨 기준치 가져오기
    public List<StandardModel> getDustStandard () {

        List<StandardModel> standardModelList = standardRepository.findAll();

        // 없을 경우 테이블 생성
        if (standardModelList.isEmpty()) {
            initializeStandardTable();
            standardModelList = standardRepository.findAll(); // 초기 데이터 넣은 후 다시 가져오기
        }

        return standardModelList;
    }

    
    // 주의보, 경보 초기 데이터 생성
    private void initializeAlertStandardTable() {
        AlertModel alertModel = new AlertModel();
        alertModel.setDustType(DustType.Fine);
        alertModel.setAlertGrade1(150);
        alertModel.setGrade1ThresholdTime(2);
        alertModel.setAlertGrade2(300);
        alertModel.setGrade2ThresholdTime(2);
        alertModel.setReleaseGrade1(100);
        alertModel.setReleaseGrade2(150);

        alertRepository.save(alertModel);

        alertModel = new AlertModel();
        alertModel.setDustType(DustType.Ultra);
        alertModel.setAlertGrade1(75);
        alertModel.setGrade1ThresholdTime(2);
        alertModel.setAlertGrade2(150);
        alertModel.setGrade2ThresholdTime(2);
        alertModel.setReleaseGrade1(35);
        alertModel.setReleaseGrade2(75);

        alertRepository.save(alertModel);
    }

    // 주의보, 경보 기준치 가져오기
    public List<AlertModel> getDustAlertStandard() {
        
        List<AlertModel> alertModelList = alertRepository.findAll();

        // 없을 경우 테이블 생성
        if (alertModelList.isEmpty()) {
            initializeAlertStandardTable();
            alertModelList = alertRepository.findAll();
        }
        
        return alertModelList;
    }
}