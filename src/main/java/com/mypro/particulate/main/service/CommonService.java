package com.mypro.particulate.main.service;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.repository.CommonRepository;

/*
 * 2025-01-02 생성
 * 
 * 기타 공통 서비스
 */
@Service
public class CommonService {
    
    private final CommonRepository commonRepository;

    public CommonService (CommonRepository commonRepository) {
        this.commonRepository = commonRepository;
    }
    
    // 테이블 조회
    public boolean isTableExistis (String tableName) {
        long count = commonRepository.isTableExists(tableName);
        return count > 0;
    }

}
