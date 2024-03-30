package com.exem.particulate.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParticulateMapper {

    // 점검일 삽입
    void insertInspection(List<String> csvList);

    // 미세먼지 등급 삽입
    void insertAlertInfo(List<String> csvList);

    // 미세먼지 농도 삽입 전 중복 체크
    int duplPartInfo(List<String> csvList);

    // 영업소별 미세먼지 농도 삽입
    void insertPartInfo(List<String> csvList);

}
