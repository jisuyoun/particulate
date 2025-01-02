package com.mypro.particulate.main.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 2024-12-23 파일생성
 * 2025-01-02 ALERT_DUST_VALUE 테이블로 변경
 * 
 * 미세먼지 및 초미세먼지 농도가 좋음 단계를 넘었을 경우 테이블에 해당 내용을 저장한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ALERT_DUST_VALUE")
public class DustModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Long id;

    private String date;
    private String time;
    private String station;
    private int fineDust;
    private int ultraFineDust;
}