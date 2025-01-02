package com.mypro.particulate.main.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/*
 * 2025-01-02 파일 생성
 * 
 * 기타 공통 쿼리문 실행 및 엔티티를 사용하지 않을 경우 Repository
 */
@Repository
public class CommonRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    // 테이블 존재 확인
    public long isTableExists (String tableName) { 
        return (long) entityManager.createNativeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = :tableName")
                .setParameter("tableName", tableName)
                .getSingleResult();
    }
}
