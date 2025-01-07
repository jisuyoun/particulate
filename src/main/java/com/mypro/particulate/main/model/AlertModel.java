package com.mypro.particulate.main.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 2025-01-07
 * 
 * 주의보, 경보가 뜬 데이터들 DB에 저장
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DUST_ALERT_VALUE")
public class AlertModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Long id;
    
    @Column(name = "date", length = 20, nullable = false, columnDefinition = "VARCHAR(20) COMMENT '발생한 일시'")
    private String date;     // 발생 일시

    @Column(name = "station", length = 10, nullable = false, columnDefinition = "VARCHAR(10) COMMENT '영업소'")
    private String station;  // 영업소

    @Enumerated(EnumType.STRING)
    @Column(name = "dust_type", length = 5, nullable = false, columnDefinition = "VARCHAR(5) COMMENT '미세먼지 타입'" )
    private DustType dustType;    // 미세먼지 타입(미세먼지, 초미세먼지)

    @Column(name = "grade", length = 10, nullable = false, columnDefinition = "VARCHAR(10) COMMENT '발생한 등급'")
    private String grade;    // 등급

    @Column(name = "value", nullable = false, columnDefinition = "FLOAT COMMENT '먼지 농도'")
    private float value;       // 농도
}
