package com.mypro.particulate.main.model;

import jakarta.persistence.Column;
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
 * 2025-01-03 파일생성
 * 
 * 미세먼지 및 초미세먼지 농도를 서버로 이동시키기 위한 모델
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Dust_VALUE")
public class DustModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", length = 20, nullable = false, columnDefinition = "VARCHAR(20) COMMENT '발생한 일시'")
    private String date;  // 일시

    @Column(name = "station", length = 10, nullable = false, columnDefinition = "VARCHAR(10) COMMENT '영업소'")
    private String station;  // 영업소

    @Column(name = "fine_dust", length = 5, nullable = false, columnDefinition = "INT COMMENT '미세먼지 농도'")
    private int fineDust; // 미세먼지 농도

    @Column(name = "ultra_fine_dust", length = 5, nullable = false, columnDefinition = "INT COMMENT '초미세먼지 농도'")
    private int ultraFineDust; // 초미세먼지 농도
}
