package com.exem.particulate.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exem.particulate.mapper.ParticulateMapper;
import com.exem.particulate.model.PartInfo;

import lombok.AllArgsConstructor;

/*
 * 24.04.11 WebHook 적용 시작
 * - 24.04.13 중복 체크까지 수정 완료
 * - 24.4.15 영업소 농도 삽입까지 완료
 */
@Service
@AllArgsConstructor
public class MainService {

    private final ParticulateMapper partMapper;
    private final TableService tableService;
    private final AlertParticulate alertParticulate;
    private final WebHookService webHookService;

    private final Logger log = LoggerFactory.getLogger(MainService.class);

    /*
     * 처음 실행시키는 것인지 확인 후 테이블 생성
     * @ throws Exception
     */
    @Transactional
    public void checkTable() {

        /*
         * 테이블이 존재하는지 확인
         * 테이블이 존재하지 않을 경우 필요한 테이블들을 만들어준다.
         */
        int tableYN = partMapper.checkTable(); 
        
        if (tableYN == 0) {
            tableService.checkTable();
        }

        try {
            alertParticulate.alertParticulate();
        } catch (Exception e) {
            log.error("MeasureParticualte에서 오류 => {}", e);
        }

    }
}
