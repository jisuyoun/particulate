package com.mypro.particulate.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.AlertStandardModel;
import com.mypro.particulate.main.model.DustType;
import com.mypro.particulate.main.model.GradeStandardModel;
import com.mypro.particulate.main.repository.AlertStandardRepository;
import com.mypro.particulate.main.repository.StandardRepository;

/*
 * 2024-12-23 파일 생성
 * 
 * 미세먼지 기준치 관련 서비스
 */
@Service
public class StandardService {

    private final StandardRepository standardRepository;
    private final AlertStandardRepository alertStandardRepository;

    public StandardService (StandardRepository standardRepository, AlertStandardRepository alertStandardRepository) {
        this.standardRepository = standardRepository;
        this.alertStandardRepository = alertStandardRepository;
    }

    // 좋음, 보통, 나쁨, 매우나쁨 초기 데이터 생성
    private void initializeStandardTable () {
        // PM10 데이터 삽입
        GradeStandardModel gradeStandardModel = new GradeStandardModel();
        gradeStandardModel.setDustType(DustType.Fine);
        gradeStandardModel.setGrade1(30);
        gradeStandardModel.setGrade2(81);
        gradeStandardModel.setGrade3(150);
        gradeStandardModel.setGrade4(151);

        standardRepository.save(gradeStandardModel);

        // PM2.5 데이터 삽입
        gradeStandardModel = new GradeStandardModel();
        gradeStandardModel.setDustType(DustType.Ultra);
        gradeStandardModel.setGrade1(15);
        gradeStandardModel.setGrade2(35);
        gradeStandardModel.setGrade3(75);
        gradeStandardModel.setGrade4(76);

        standardRepository.save(gradeStandardModel);
   }

    // 좋음, 보통, 나쁨, 매우나쁨 기준치 가져오기
    public List<GradeStandardModel> getDustStandard () {

        List<GradeStandardModel> standardModelList = standardRepository.findAll();

        // 없을 경우 테이블 생성
        if (standardModelList.isEmpty()) {
            initializeStandardTable();
            standardModelList = standardRepository.findAll(); // 초기 데이터 넣은 후 다시 가져오기
        }

        return standardModelList;
    }

    
    // 주의보, 경보 초기 데이터 생성
    private void initializeAlertStandardTable() {
        AlertStandardModel AlertStandardModel = new AlertStandardModel();
        AlertStandardModel.setDustType(DustType.Fine);
        AlertStandardModel.setAlertGrade1(150);
        AlertStandardModel.setGrade1ThresholdTime(2);
        AlertStandardModel.setAlertGrade2(300);
        AlertStandardModel.setGrade2ThresholdTime(2);
        AlertStandardModel.setReleaseGrade1(100);
        AlertStandardModel.setReleaseGrade2(150);

        alertStandardRepository.save(AlertStandardModel);

        AlertStandardModel = new AlertStandardModel();
        AlertStandardModel.setDustType(DustType.Ultra);
        AlertStandardModel.setAlertGrade1(75);
        AlertStandardModel.setGrade1ThresholdTime(2);
        AlertStandardModel.setAlertGrade2(150);
        AlertStandardModel.setGrade2ThresholdTime(2);
        AlertStandardModel.setReleaseGrade1(35);
        AlertStandardModel.setReleaseGrade2(75);

        alertStandardRepository.save(AlertStandardModel);
    }

    // 주의보, 경보 기준치 가져오기
    public List<AlertStandardModel> getDustAlertStandard() {
        
        List<AlertStandardModel> alertModelList = alertStandardRepository.findAll();

        // 없을 경우 테이블 생성
        if (alertModelList.isEmpty()) {
            initializeAlertStandardTable();
            alertModelList = alertStandardRepository.findAll();
        }
        
        return alertModelList;
    }
}