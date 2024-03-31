package com.exem.particulate.service;

public interface ParticulateService {

    // 처음 실행시키는 것인지 확인
    void checkTable() throws Exception;

    // 측정농도를 DB에 저장
    void particulate() throws Exception;

}
