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
 * 24-12-30 생성
 * 
 * 미세먼지 및 초미세먼지 기준치 테이블인 STANDARDS의 MODEL
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="STANDARDS")
public class StandardModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Long id;

    @Column(name="dust_type", length=20, nullable=false, columnDefinition="VARCHAR(10) COMMENT '미세먼지 타입'")
    private String dustType;

    @Column(name="grade1", length=3, nullable=false, columnDefinition="INT COMMENT '좋음의 최대값'")
    private int grade1;

    @Column(name="grade2", length=3, nullable=false, columnDefinition="INT COMMENT '보통의 최대값'")
    private int grade2;

    @Column(name="grade3", length=3, nullable=false, columnDefinition="INT COMMENT '나쁨의 최대값'")
    private int grade3;

    @Column(name="grade4", length=3, nullable=false, columnDefinition="INT COMMENT '매우나쁨의 최대값'")
    private int grade4;
}
