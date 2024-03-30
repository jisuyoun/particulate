package com.exem.particulate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

public class ParticulateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testParticulate() {

        List<String> csvList = new ArrayList<>();
        csvList.add("2022-02-01 20");
        csvList.add("중구");
        csvList.add("12543");
        csvList.add("100");
        csvList.add("10");

        try {
            mockMvc.perform(get("/insertParticulate"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
