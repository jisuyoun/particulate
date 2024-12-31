package com.mypro.particulate.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.DustModel;
import com.mypro.particulate.main.model.StandardModel;
import com.mypro.particulate.main.repository.StandardRepository;

/*
 * 2024-12-23 파일 생성
 * DustService
 */
@Service
public class DustService {

    private final StandardRepository standardRepository;

    public DustService(StandardRepository standardRepository) {
        this.standardRepository = standardRepository;
    }

    public void saveDustData(DustModel dustModel) {
        //dustRepository.save(dustModel);
    }

    // 기준치 가져오기
    public List<StandardModel> getDustStandard() {
        return standardRepository.findAll();
    }
}
