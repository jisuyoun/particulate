package com.mypro.particulate.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.DustType;
import com.mypro.particulate.main.model.StandardModel;
import com.mypro.particulate.main.repository.StandardRepository;

/*
 * 2024-12-23 파일 생성
 * 
 * 미세먼지 기준치 관련 서비스
 */
@Service
public class StandardService {

    private final StandardRepository standardRepository;

    public StandardService (StandardRepository standardRepository) {
        this.standardRepository = standardRepository;
    }

    // 초기 데이터 생성
    public void initializeStandardTable () {
         // PM10 데이터 삽입
         StandardModel pm10Model = new StandardModel();
         pm10Model.setDustType(DustType.Fine);
         pm10Model.setGrade1(30);
         pm10Model.setGrade2(81);
         pm10Model.setGrade3(150);
         pm10Model.setGrade4(151);

         standardRepository.save(pm10Model);

         // PM2.5 데이터 삽입
         StandardModel pm25Model = new StandardModel();
         pm25Model.setDustType(DustType.Ultra);
         pm25Model.setGrade1(15);
         pm25Model.setGrade2(35);
         pm25Model.setGrade3(75);
         pm25Model.setGrade4(76);

         standardRepository.save(pm25Model);
    }

    // 기준치 가져오기
    public List<StandardModel> getDustStandard () {

        List<StandardModel> standardModelList = standardRepository.findAll();

        // 없을 경우 테이블 생성
        if (standardModelList.isEmpty()) {
            initializeStandardTable();
            standardModelList = standardRepository.findAll(); // 초기 데이터 넣은 후 다시 가져오기
        }

        return standardModelList;
    }
}