package com.exem.particulate.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.exem.particulate.mapper.ParticulateMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticulateServiceImpl implements ParticulateService {
    
    private final ParticulateMapper partMapper;

    @Override
    public Map<String, Object> selectTableYN() {
        System.out.println("Mapper 메소드 호출 전");
        Map<String, Object> result = partMapper.selectTableYN();
        System.out.println(result);
        System.out.println("Mapper 메소드 호출 후");
        return result;
    }

}
