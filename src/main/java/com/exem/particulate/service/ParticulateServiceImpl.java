package com.exem.particulate.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.exem.particulate.mapper.ParticulateMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticulateServiceImpl implements ParticulateService {
    
    private final ParticulateMapper partMapper;

    // 영업소별 미세먼지 농도 삽입
    @Override
    public void insertPartInfo(List<String> csvList) throws Exception {
        partMapper.insertPartInfo(csvList);
    }

    // 점검일 삽입
    @Override
    public void insertInspection(List<String> csvList, String inspectionType) throws Exception {

        switch (inspectionType) {
            case "dual": 
                inspectionType = "모든 측정기";
                break;

            case "part":
                inspectionType = "미세먼지 측정기";
                break;
            
            default:
                inspectionType = "초미세먼지 측정기";
                break;
        }

        csvList.add(inspectionType);

        partMapper.insertInspection(csvList);
    }

    // 미세먼지 등급 삽입
    @Override
    public void insertAlertInfo(List<String> csvList) throws Exception {
        partMapper.insertAlertInfo(csvList);
    }


}
