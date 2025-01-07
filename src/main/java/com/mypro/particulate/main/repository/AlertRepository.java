package com.mypro.particulate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mypro.particulate.main.model.AlertModel;

/*
 * 2025-01-07
 * 
 * 주의보, 경보가 뜬 데이터들 DB에 저장 Repository
 */
public interface AlertRepository extends JpaRepository<AlertModel, Long>  {
    
}
