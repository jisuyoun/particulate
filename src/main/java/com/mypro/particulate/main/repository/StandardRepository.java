package com.mypro.particulate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mypro.particulate.main.model.StandardModel;

/*
 * 2024-12-30 파일 생성
 * 
 * 미세먼지 및 초미세먼지 기준치 관련 Repository
 */
public interface StandardRepository extends JpaRepository<StandardModel, Long> {
    
}
