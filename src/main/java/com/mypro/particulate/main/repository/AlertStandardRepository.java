package com.mypro.particulate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mypro.particulate.main.model.AlertStandardModel;

/*
 * 2025-01-06 파일 생성
 * 
 * 경보, 주의보에 대한 경보 관련 Repository
 */
public interface AlertStandardRepository extends JpaRepository<AlertStandardModel, Long> {
    
}
