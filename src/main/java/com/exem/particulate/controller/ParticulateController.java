package com.exem.particulate.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.exem.particulate.service.MainService;

@RestController 
@RequiredArgsConstructor
public class ParticulateController {
    
    private final Logger log = LoggerFactory.getLogger(ParticulateController.class);
    private final MainService mainService;

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebHook(@RequestBody Map<String, Object> notification) {

        mainService.checkTable();
        
        // 처리 성공 응답 보내기
        return new ResponseEntity<>(notification+ " / 데이터 수신", HttpStatus.OK);
    }

}
