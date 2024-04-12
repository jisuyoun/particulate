package com.exem.particulate.service;

import java.io.BufferedReader;
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

import com.exem.particulate.mapper.ParticulateMapper;
import com.exem.particulate.model.PartInfo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MeasureParticulate {

    private final ParticulateMapper partMapper;
    
    private final Logger log = LoggerFactory.getLogger(MeasureParticulate.class);

    /*
     * 측정농도를 DB에 저장
     * @ throws Exception
     */
    public void insertParticulate() throws Exception {
    
        log.info("======= particulate 시작 ======");

        Path dir = Paths.get("particulate\\src\\main\\resources\\csv");

        try (DirectoryStream<Path> csvFile = Files.newDirectoryStream(dir, "*.csv")) {
            for (Path File : csvFile) {
                try (BufferedReader br = Files.newBufferedReader(File)) {

                    br.readLine(); // 첫 줄은 컬럼명이므로 제외하고 읽는다.

                    String line;

                    int grade1Cnt = 0;
                    int grade2Cnt = 0;
                    int grade3Cnt = 0;
                    int grade4Cnt = 0;

                    while ((line = br.readLine()) != null) {

                        String[] array = line.split(",");

                        List<String> csvList = new ArrayList<>(Arrays.asList(array));

                        // 24시를 다음날 00시로 변경 및 지역명 추출
                        csvList = changeDate(csvList, File);

                        PartInfo particulateDTO = createPartDTO(csvList);

                        // 데이터를 작업하기 전 중복체크를 진행한다.
                        boolean partFlag = duplPartInfo(particulateDTO);

                        System.out.println(partFlag);
                        System.out.println(particulateDTO.getStation());
                        System.out.println(particulateDTO.getRegDt());
                        
                        if (partFlag) {
                            log.info("[정보] {} {} {}시 데이터는 중복입니다.", new Object[] {
                                csvList.get(0),
                                csvList.get(2),
                                csvList.get(1)
                            });
                            continue;
                        }
                        
                    }
                }
            }
        }
    }

    /*
     * 24시를 다음날 00시로 변경 및 지역명 추출 
     * @ param csvList[0] = cityName
     * @ param csvList[1] = dateTimeStr
     * @ param csvList[2] = station
     * @ param csvList[3] = stationCode
     * @ param csvList[4] = particulate
     * @ param csvList[5] = fineParticualte
     */
    private List<String> changeDate(List<String> csvList, Path File) {
        
        String dateTimeStr = csvList.get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

        if (dateTime.getHour() == 24) {
            dateTime = dateTime.withHour(0).plusDays(1);
        }

        dateTimeStr = dateTime.format(formatter); // 다시 문자열로 변환

        csvList.set(0, dateTimeStr);

        String FileNm = File.getFileName().toString();
        int FileNmIdx = FileNm.split("_")[1].indexOf("시");
        String cityNm = FileNm.split("_")[1].substring(0, FileNmIdx);

        csvList.add(0, cityNm);

        return csvList;
    }

    private PartInfo createPartDTO(List<String> csvList) {
        String city = csvList.get(0);
        String regDt = csvList.get(1);
        String station = csvList.get(2);
        //int particulate = Integer.parseInt(csvList.get(4));
        //int fineParticulate = Integer.parseInt(csvList.get(5));
    
        // return new PartInfo(city, regDt, station, particulate, fineParticulate);
        return new PartInfo(city, regDt, station);
    }

    /*
     * 중복된 데이터가 있는지 확인
     * @ param csvList[0] = cityName
     * @ param csvList[1] = dateTimeStr
     * @ param csvList[2] = station
     * @ param csvList[3] = stationCode
     * @ param csvList[4] = particulate
     * @ param csvList[5] = fineParticualte
     */
    private boolean duplPartInfo(PartInfo particulateDTO) {
        
        boolean result = false;

        try {
            int duplChk = partMapper.duplPartInfo(particulateDTO);

            if (duplChk != 0) {
                result = true;
            }
        } catch (Exception e) {
            log.error("[에러] 중복 검사 중 에러 발생 =>  " + particulateDTO);
            log.error("[에러] 내용 => " + e);
            e.printStackTrace();
        }
        return result;
    }
}
