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
 * 24-12-30 생성
 * 
 * 미세먼지 및 초미세먼지 기준치 테이블인 STANDARDS의 MODEL
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GRADE_STANDARDS")
public class GradeStandardModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "dust_type", length = 5, nullable = false, columnDefinition = "VARCHAR(5) COMMENT '미세먼지 타입'")
    private DustType dustType;

    @Column(name = "grade1", nullable = false, columnDefinition = "FLOAT COMMENT '좋음의 최대값'")
    private float grade1;

    @Column(name = "grade2", nullable=false, columnDefinition="FLOAT COMMENT '보통의 최대값'")
    private float grade2;

    @Column(name="grade3", nullable=false, columnDefinition="FLOAT COMMENT '나쁨의 최대값'")
    private float grade3;

    @Column(name="grade4", nullable=false, columnDefinition="FLOAT COMMENT '매우나쁨의 최대값'")
    private float grade4;
}
