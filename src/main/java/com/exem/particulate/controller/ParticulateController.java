package com.exem.particulate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exem.particulate.service.ParticulateService;


@RestController 
@RequiredArgsConstructor
public class ParticulateController {
    
    private final ParticulateService partService;

    private final Logger log = LoggerFactory.getLogger(ParticulateController.class);

    /*
     * 측정농도를 DB에 저장
     */
    @PostMapping("/execute")
    public void execute() {

        try {
            // 처음 실행시키는 것인지 확인
            partService.checkTable();

            // DB 저장 시작
            partService.particulate();
        } catch (Exception e) {
            log.error("Controller 오류 발생 " + e);
            e.printStackTrace();
        }

        
    }

}
