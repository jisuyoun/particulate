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

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AlertParticulate {

    private final ParticulateMapper partMapper;
    private final InsertParticulate insertParticulate;
    private final InspectionStation inspectionStation;
    
    private final Logger log = LoggerFactory.getLogger(AlertParticulate.class);

    /*
     * 측정농도를 DB에 저장하기 전 작업 및 중복체크
     * @ throws Exception
     */
    public void alertParticulate() throws Exception {
    
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

                        // 데이터를 작업하기 전 중복체크를 진행한다.
                        boolean partFlag = duplPartInfo(csvList);
                        
                        if (partFlag) {
                            log.info("[정보] {} {} {}시 데이터는 중복입니다.", new Object[] {
                                csvList.get(0),
                                csvList.get(2),
                                csvList.get(1)
                            });
                            continue;
                        }

                        // 점검일이 있는 경우 농도를 0으로 바꿔준 csvList
                        csvList = inspectionStation.insertInspection(csvList);

                        int partValue = Integer.parseInt(csvList.get(4));

                        if (partValue >= 300) {
                            // 미세먼지 경보일 경우, 주의보는 cnt 0으로 바꿔준다.
                            grade2Cnt++;
                            grade4Cnt = 0;
                        } else if (partValue < 300 && partValue >= 150) {
                            // 미세먼지 주의보일 경우
                            grade2Cnt = 0;
                            grade4Cnt++;
                        } else {
                            // 미세먼지 경보도 주의보도 아닐 경우
                            grade2Cnt = 0;
                            grade4Cnt = 0;
                        }

                        int fineValue = Integer.parseInt(csvList.get(5));

                        if (fineValue >= 150) {
                            // 초미세먼지 경보일 경우, 주의보는 cnt 0으로 만들어준다.
                            grade1Cnt++;
                            grade3Cnt = 0;
                        } else if (fineValue < 150 && fineValue >= 75) {
                            // 초미세먼지 주의보일 경우
                            grade1Cnt = 0;
                            grade3Cnt++;
                        } else {
                            // 초미세먼지 경보도 주의보도 아닐 경우
                            grade1Cnt = 0;
                            grade3Cnt = 0;
                        }
                        if (grade1Cnt >= 2 || grade2Cnt >= 2 
                            || grade3Cnt >= 2 || grade4Cnt >= 2) {

                                List<Integer> gradeList = new ArrayList<>();
                                gradeList.add(grade1Cnt);
                                gradeList.add(grade2Cnt);
                                gradeList.add(grade3Cnt);
                                gradeList.add(grade4Cnt);

                                // 미세먼지의 등급을 알아본다.
                                alertGrade(gradeList, csvList);
                        }

                        insertParticulate.insertParticualte(csvList);
                        
                    }
                }
            }
        }
    }

    /*
     * 미세먼지 및 초미세먼지 등급 insert
     * @ param csvList[0] = cityName
     * @ param csvList[1] = dateTimeStr
     * @ param csvList[2] = station
     * @ param csvList[3] = stationCode
     * @ param csvList[4] = particulate
     * @ param csvList[5] = fineParticualte
     */
    private void alertGrade(List<Integer> gradeList, List<String> csvList) {
        String grade = "";

        int grade1Cnt = gradeList.get(0);
        int grade2Cnt = gradeList.get(1);
        int grade3Cnt = gradeList.get(2);
        int grade4Cnt = gradeList.get(3);

        if (grade1Cnt >= 2) {
            grade = "1";
        } else if (grade2Cnt >= 2) {
            grade = "2";
        } else if (grade3Cnt >= 2) {
            grade = "3";
        } else if (grade4Cnt >= 2) {
            grade = "4";
        } else {
            grade = "";
        }

        if (grade != "") {
            log.info("[정보] {}시 {} {}시 현재 대기 등급 {} 입니다.", new Object[] {
                csvList.get(0),
                csvList.get(2),
                csvList.get(1),
                grade
            });

            try {

                csvList.add(grade); // 리스트에 등급을 추가로 넣어준다.

                // 미세먼지 등급을 기록한다.
                partMapper.insertAlertInfo(csvList);

            } catch (Exception e) {
                log.error("[에러] 미세먼지 경보 중 에러 발생 => " + csvList);
                log.error("[에러] 내용 => " + e);
                e.printStackTrace();
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

    /*
     * 중복된 데이터가 있는지 확인
     * @ param csvList[0] = cityName
     * @ param csvList[1] = dateTimeStr
     * @ param csvList[2] = station
     * @ param csvList[3] = stationCode
     * @ param csvList[4] = particulate
     * @ param csvList[5] = fineParticualte
     */
    private boolean duplPartInfo(List<String> csvList) {
        
        boolean result = false;

        try {
            int duplChk = partMapper.duplPartInfo(csvList);

            if (duplChk != 0) {
                result = true;
            }
        } catch (Exception e) {
            log.error("[에러] 중복 검사 중 에러 발생 =>  " + csvList);
            log.error("[에러] 내용 => " + e);
            e.printStackTrace();
        }
        return result;
    }
}
