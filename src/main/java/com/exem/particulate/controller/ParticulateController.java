package com.exem.particulate.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;

import com.exem.particulate.service.ParticulateService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class ParticulateController {
    
    private final ParticulateService partService;

    @GetMapping("/path")
    public Map<String, Object> selectTableYN() {
        System.out.println("controller 호출전");

        Map<String, Object> result = partService.selectTableYN();

        System.out.println(result.get("YN"));

        return result;
    }
    

}
