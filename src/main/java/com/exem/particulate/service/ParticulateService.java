package com.exem.particulate.service;

import java.util.List;

public interface ParticulateService {

    // 영업소별 미세먼지 농도 삽입
    void insertPartInfo(List<String> csvList) throws Exception;

    // 점검일 삽입
    void insertInspection(List<String> csvList, String inspectionType) throws Exception;

    // 미세먼지 등급 삽입
    void insertAlertInfo(List<String> csvList) throws Exception;
}
