package com.mypro.particulate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mypro.particulate.main.model.StandardModel;

/*
 * 24-12-30 생성
 */
public interface StandardRepository extends JpaRepository<StandardModel, Long> {
    
}
