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
 * 2025-01-06 파일 생성
 * 
 * 경보, 주의보에 대한 발령 관련 Model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DUST_ALERT_VALUE")
public class AlertModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "dust_type", length = 5, nullable = false, columnDefinition = "VARCHAR(5) COMMENT '미세먼지 타입'")
    private DustType dustType;
    
    @Column(name="alert_grade1", nullable=false, columnDefinition="FLOAT COMMENT '주의보 최대값'")
    private float alertGrade1;

    @Column(name="grade1_threshold_time", nullable=false, columnDefinition="INT COMMENT '주의보 설정시간'")
    private int grade1ThresholdTime;

    @Column(name="alert_grade2", nullable=false, columnDefinition="FLOAT COMMENT '경보 최대값'")
    private float alertGrade2;

    @Column(name="grade2_threshold_time", nullable=false, columnDefinition="INT COMMENT '경보 설정시간'")
    private int grade2ThresholdTime;

    @Column(name="release_grade1", nullable=false, columnDefinition="FLOAT COMMENT '주의보 해제 최대값'")
    private float releaseGrade1;

    @Column(name="release_grade2", nullable=false, columnDefinition="FLOAT COMMENT '경보 해제 최대값'")
    private float releaseGrade2;
}
