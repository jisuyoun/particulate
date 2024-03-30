package com.exem.particulate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.exem.particulate.service.ParticulateService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class ParticulateController {
    
    private final ParticulateService partService;

    private final Logger log = LoggerFactory.getLogger(ParticulateController.class);

    /*
     * 측정농도를 DB에 저장
     */
    @GetMapping("/insertParticulate")
    public void particulate() {

        try {
            partService.particulate();
        } catch (Exception e) {
            log.error("Controller 오류 발생");
            e.printStackTrace();
        }
        
    }

    

}
