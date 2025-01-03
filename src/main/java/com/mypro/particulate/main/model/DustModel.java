package com.mypro.particulate.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 2025-01-03 파일생성
 * 
 * 미세먼지 및 초미세먼지 농도를 서버로 이동시키기 위한 모델
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DustModel {
    private String date;  // 일시
    private String station;  // 영업소
    private int fineDust; // 미세먼지 농도
    private int ultraFineDust; // 초미세먼지 농도
}
