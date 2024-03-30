package com.exem.particulate.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.exem.particulate.mapper.ParticulateMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticulateServiceImpl implements ParticulateService {
    
    private final ParticulateMapper partMapper;

    private final Logger log = LoggerFactory.getLogger(ParticulateServiceImpl.class);

    /*
     * 측정농도를 DB에 저장
     */
    @Override
    public void particulate() {

        try {
            log.info("======= particulate 시작 ======");

            Path dir = Paths.get("src\\main\\resources\\csv");

            try (DirectoryStream<Path> csvFile = Files.newDirectoryStream(dir, "*.csv")) {
                for (Path entry : csvFile) {
                    try (BufferedReader br = Files.newBufferedReader(entry)) {

                        br.readLine(); // 첫 줄은 컬럼명이므로 제외하고 읽는다.

                        String line;

                        int grade1Cnt = 0;
                        int grade2Cnt = 0;
                        int grade3Cnt = 0;
                        int grade4Cnt = 0;

                        Map<String, Integer> gradeMap = new HashMap<>();

                        while ((line = br.readLine()) != null) {
                            
                            String[] array = line.split(",");

                            List<String> csvList = new ArrayList<>(Arrays.asList(array));

                            // ==== 24시를 다음날 00시로 변경해준다. start ==== //
                            String dateTimeStr =csvList.get(0);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

                            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
                            
                            if (dateTime.getHour() == 24) {
                                dateTime = dateTime.withHour(0).plusDays(1);
                            }

                            dateTimeStr = dateTime.format(formatter); // 다시 문자열로 변환

                            csvList.set(0, dateTimeStr);
                            // ==== 24시를 다음날 00시로 변경해준다. end ==== //
                           
                            String inspectionType = ""; // 측정기 점검 여부

                            try {
                                List<String> modifiableList = new ArrayList<>(csvList);

                                if (csvList.size() == 3) {
                                    // 측정 농도가 없을 경우에는 점검 날로 가정한다.
                                    csvList.add("0");
                                    csvList.add("0");

                                    inspectionType = "dual";
                                    
                                } else {
                                    try {
                                        if (modifiableList.get(3).isEmpty()) {
                                            // 측정 농도 중 미세먼지 측정값만 없을 경우 미세먼지 측정기 점검 날로 가정한다.
                                            modifiableList.set(3, "0");

                                            inspectionType = "part";

                                        }
                                    
                                        if (modifiableList.size() < 5 || modifiableList.get(4).isEmpty()) {
                                            // 측정 농도 중 초미세먼지 측정값만 없을 경우 초미세먼지 측정기 점검 날로 가정한다.
                                            inspectionType = "fine";

                                            if (modifiableList.size() < 5) {
                                                modifiableList.add("0");

                                            } else {
                                                modifiableList.set(4, "0");

                                            }
                                        }

                                        csvList = modifiableList; // 점검일에 0을 넣은 리스트로 변경
                                    } catch (Exception e) {
                                        log.error("에러 => 점검일 농도 0으로 만드는 중 에러 발생");
                                        e.printStackTrace();
                                    }
                                    try {
                                        if (!"".equals(inspectionType)) {
                                            // 점검 정보를 insert
                                            switch (inspectionType) {
                                                case "dual": 
                                                    inspectionType = "모든 측정기";
                                                    break;
                                    
                                                case "part":
                                                    inspectionType = "미세먼지 측정기";
                                                    break;
                                                
                                                default:
                                                    inspectionType = "초미세먼지 측정기";
                                                    break;
                                            }
                                    
                                            modifiableList.add(inspectionType);
    
                                            partMapper.insertInspection(modifiableList);
                                        }
                                    } catch (Exception e) {
                                        log.error("에러 => 점검일 DB 등록 중 에러 발생");
                                    }
                                    
                                    if (Integer.parseInt(csvList.get(3)) >= 300) {
                                        // 미세먼지 경보일 경우, 주의보는 cnt 0으로 바꿔준다.
                                        grade2Cnt++;
                                        grade4Cnt = 0;
                                    } else if (Integer.parseInt(csvList.get(3)) >= 150) {
                                        // 미세먼지 주의보일 경우
                                        grade2Cnt++;
                                        grade4Cnt++;
                                    } else {
                                        // 미세먼지 경보도 주의보도 아닐 경우
                                        grade2Cnt = 0;
                                        grade4Cnt = 0;
                                    }
                            
                                    if (Integer.parseInt(csvList.get(4)) >= 150) {
                                        // 초미세먼지 경보일 경우, 주의보는 cnt 0으로 만들어준다.
                                        grade1Cnt++;
                                        grade3Cnt = 0;
                                    } else if (Integer.parseInt(csvList.get(4)) >= 75) {
                                        // 초미세먼지 주의보일 경우
                                        grade1Cnt++;
                                        grade3Cnt++;
                                    } else {
                                        // 초미세먼지 경보도 주의보도 아닐 경우
                                        grade1Cnt = 0;
                                        grade3Cnt = 0;
                                    }

                                    gradeMap.put("grade1Cnt", grade1Cnt);
                                    gradeMap.put("grade2Cnt", grade2Cnt);
                                    gradeMap.put("grade3Cnt", grade3Cnt);
                                    gradeMap.put("grade4Cnt", grade4Cnt);

                                    // 미세먼지 등급을 알려주며, 등급을 저장한다.
                                    alertGrade(gradeMap, csvList);
                                }
                                
                                // 각 측정소 별 미세먼지와 초미세먼지 농도를 삽입한다.
                                partMapper.insertPartInfo(csvList);

                            } catch (Exception e) {
                                log.error("에러 => 측정소별 농도 넣는 중 에러 발생");
                                e.printStackTrace();
                            }
                        }                        

                    }
                }
            }
        } catch (IOException e) {
            log.error("에러 => csv 파일 찾는 중 에러 발생");
            System.out.println(e);
        }

    }

    // 미세먼지 등급을 알려주며, 등급을 저장한다.
    private void alertGrade(Map<String, Integer> gradeMap, List<String> csvList) {
            
        String grade = "";

        int grade1Cnt = gradeMap.get("grade1Cnt");
        int grade2Cnt = gradeMap.get("grade2Cnt");
        int grade3Cnt = gradeMap.get("grade3Cnt");
        int grade4Cnt = gradeMap.get("grade4Cnt");

        if (grade1Cnt >= 2) {
            grade = "1";
        } else if (grade2Cnt >= 2) {
            grade = "2";
        } else if (grade3Cnt >= 3) {
            grade = "3";
        } else if (grade4Cnt >= 4) {
            grade = "4";
        } else {
            grade = "";
        }

        if (grade != "") {

            log.info(csvList.get(0) + "시 " + csvList.get(1) + " 영업소는 현재 대기 등급 " + grade + " 입니다.");

            try {
                
                csvList.add(grade); // 리스트에 등급을 추가로 넣어준다.

                // 미세먼지 등급을 기록한다.
                partMapper.insertAlertInfo(csvList);

            } catch (Exception e) {
                log.error("에러 => 미세먼지 경보 중 에러 발생");
                e.printStackTrace();
            }
        }
    }
}
