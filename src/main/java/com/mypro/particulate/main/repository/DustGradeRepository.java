package com.mypro.particulate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mypro.particulate.main.model.DustGradeModel;

/*
 * 2025-01-03 파일 생성
 * 
 * 보통 이상의 농도를 DB에 저장하는 Repository
 */
public interface DustGradeRepository extends JpaRepository<DustGradeModel, Long> {
    
}
