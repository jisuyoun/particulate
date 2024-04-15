package com.exem.particulate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.particulate.mapper.ParticulateMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InsertParticulate {

    private final ParticulateMapper particulateMapper;

    /*
     * 추출된 농도 데이터를 DB에 Insert
     * @ param particulateDTO
     */
    public void insertParticualte(List<String> csvList) throws Exception {
        
        particulateMapper.insertPartInfo(csvList);

    }
    
}
