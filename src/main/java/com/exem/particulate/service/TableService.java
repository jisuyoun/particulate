package com.exem.particulate.service;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exem.particulate.mapper.ParticulateMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TableService {

    private final ParticulateMapper partMapper;

    private final Logger log = LoggerFactory.getLogger(TableService.class);

    /*
     * 처음 실행시키는 것인지 확인 후 테이블 생성
     * @ throws Exception
     */
    @Transactional
    public void checkTable() {
     
        // 지역 테이블 만들기
        partMapper.createCityTable();
        
        // 측정소 테이블 만들기
        partMapper.createStationTable();

        // 농도 테이블 만들기
        partMapper.createParticulateTable();

        // 경보 주의보 테이블 만들기
        partMapper.createAlertTable();

        // 점검 일자 테이블 만들기
        partMapper.createInspectionTable();

        List<String> cityList = new ArrayList<>();
        cityList.add("서울");
        cityList.add("경기");
        cityList.add("인천");
        cityList.add("강원");
        cityList.add("충북");
        cityList.add("충남");
        cityList.add("세종");
        cityList.add("대전");
        cityList.add("경북");
        cityList.add("경남");
        cityList.add("대구");
        cityList.add("전북");
        cityList.add("전남");
        cityList.add("광주");
        cityList.add("울산");
        cityList.add("부산");
        cityList.add("제주");

        // 지역에 대한 정보를 넣어준다.
        partMapper.insertCityInfo(cityList);

        // =========== 영업소에 대한 정보 insert start =========== //
        Path dir = Paths.get("particulate\\src\\main\\resources\\city");

        try (DirectoryStream<Path> csvFile = Files.newDirectoryStream(dir, "*.csv")) {
            for (Path File : csvFile) {
                try (BufferedReader br = Files.newBufferedReader(File, StandardCharsets.UTF_8)) {
                    // 파일 속 상단 3줄은 필요 없으므로 제외
                    br.readLine();
                    br.readLine();
                    br.readLine();

                    String line;
                    
                    while ((line = br.readLine()) != null) {
                        
                        String[] array = line.split(",");

                        List<String> csvList = new ArrayList<>(Arrays.asList(array));

                        if (!csvList.isEmpty() && cityList.contains(csvList.get(0))) {
                            // 무결성을 위해 TB_CITY_INFO 에 있는 지역들에 대한 데이터만 넣어준다.
                            partMapper.insertStationInfo(csvList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("[에러] 지역과 측정소 정보를 insert 중 에러 발생 => " + e);
            e.printStackTrace();
        }
        // =========== 영업소에 대한 정보 insert end =========== //
    
    }

}
