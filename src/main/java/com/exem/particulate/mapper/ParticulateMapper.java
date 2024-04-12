package com.exem.particulate.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.exem.particulate.model.PartInfo;

@Mapper
public interface ParticulateMapper {

    // 처음 실행시키는 것인지 확인
    int checkTable();

    // 지역 테이블 생성
    void createCityTable();

    // 측정소 테이블 생성
    void createStationTable();

    // 농도 테이블 생성
    void createParticulateTable();

    // 경보 주의보 테이블 생성
    void createAlertTable();

    // 점검 일자 테이블 생성
    void createInspectionTable();

    // 지역에 대한 정보 삽입
    void insertCityInfo(List<String> cityList);
    
    // 영업소에 대한 정보 삽입
    void insertStationInfo(List<String> csvList);

    // 점검일 삽입
    void insertInspection(List<String> csvList);

    // 미세먼지 등급 삽입
    void insertAlertInfo(List<String> csvList);

    // 미세먼지 농도 삽입 전 중복 체크
    int duplPartInfo(PartInfo particulateDTO);

    // 영업소별 미세먼지 농도 삽입
    void insertPartInfo(List<String> csvList);

}
