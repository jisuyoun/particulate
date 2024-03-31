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

import com.exem.particulate.mapper.ParticulateMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticulateServiceImpl implements ParticulateService {

    private final ParticulateMapper partMapper;

    private Logger log = LoggerFactory.getLogger(ParticulateServiceImpl.class);

    /*
     * 처음 실행시키는 것인지 확인 후 테이블 생성
     */
    @Override
    public void checkTable() throws Exception {

        int tableYN = partMapper.checkTable();

        if (tableYN == 0) {
            try {
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

                // 시에 대한 정보를 넣어준다.
                partMapper.insertCityInfo(cityList);

                Path dir = Paths.get("src\\main\\resources\\city");

                try (DirectoryStream<Path> csvFile = Files.newDirectoryStream(dir, "*.csv")) {
                    for (Path File : csvFile) {
                        try (BufferedReader br = Files.newBufferedReader(File, StandardCharsets.UTF_8)) {
                            // 파일 속 상단 3줄은 필요 없으므로 제외
                            br.readLine();
                            br.readLine();
                            br.readLine();

                            String line;

                            while ((line = br.readLine()) != null) {
                                // 영업소에 대한 정보를 넣어준다.
                                String[] array = line.split(",");

                                List<String> csvList = new ArrayList<>(Arrays.asList(array));

                                if (!csvList.isEmpty() && cityList.contains(csvList.get(0))) {
                                    // 무결성을 위해 TB_CITY_INFO 에 있는 지역들에 대한 데이터만 넣어준다.
                                    partMapper.insertStationInfo(csvList);
                                }

                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("지역과 측정소 정보를 insert 중 에러 발생");
                e.printStackTrace();
            }
        }
    }

    /*
     * 측정농도를 DB에 저장
     */
    @Override
    public void particulate() {

        try {
            log.info("======= particulate 시작 ======");

            Path dir = Paths.get("src\\main\\resources\\csv");

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

                            // ==== 24시를 다음날 00시로 변경해준다. start ==== //
                            String dateTimeStr = csvList.get(0);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

                            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

                            if (dateTime.getHour() == 24) {
                                dateTime = dateTime.withHour(0).plusDays(1);
                            }

                            dateTimeStr = dateTime.format(formatter); // 다시 문자열로 변환

                            csvList.set(0, dateTimeStr);
                            // ==== 24시를 다음날 00시로 변경해준다. end ==== //

                            // ====== 지역명 추출 start ====== //
                            String FileNm = File.getFileName().toString();
                            int FileNmIdx = FileNm.split("_")[1].indexOf("시");
                            String cityNm = FileNm.split("_")[1].substring(0, FileNmIdx);

                            csvList.add(0, cityNm);
                            // ====== 지역명 추출 end ====== //

                            // 데이터를 작업하기 전 중복체크를 진행한다.
                            boolean partFlag = duplPartInfo(csvList);

                            if (partFlag) {
                                log.info(csvList.get(1) + "시 " + csvList.get(0) + " " + csvList.get(2) + " 데이터는 중복입니다.");
                                continue;
                            }

                            String inspectionType = ""; // 측정기 점검 여부

                            try {
                                List<String> modifiableList = new ArrayList<>(csvList);

                                if (modifiableList.size() == 4) {
                                    // 측정 농도가 없을 경우에는 점검 날로 가정한다.
                                    modifiableList.add("0");
                                    modifiableList.add("0");

                                    inspectionType = "dual";

                                } else {
                                    if (modifiableList.get(4).isEmpty()) {
                                        // 측정 농도 중 미세먼지 측정값만 없을 경우 미세먼지 측정기 점검 날로 가정한다.
                                        modifiableList.set(4, "0");

                                        inspectionType = "part";

                                    }

                                    if (modifiableList.size() < 6 || modifiableList.get(4).isEmpty()) {
                                        // 측정 농도 중 초미세먼지 측정값만 없을 경우 초미세먼지 측정기 점검 날로 가정한다.
                                        inspectionType = "fine";

                                        if (modifiableList.size() < 6) {
                                            modifiableList.add("0");

                                        } else {
                                            modifiableList.set(5, "0");
                                        }
                                    }
                                }

                                csvList = modifiableList; // 점검일에 0을 넣은 리스트로 변경

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
                                    log.error("에러 발생구간 => " + modifiableList);
                                    log.error("에러 => 점검일 DB 등록 중 에러 발생" + e);
                                }

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

                                List<Integer> gradeList = new ArrayList<>();
                                gradeList.add(grade1Cnt);
                                gradeList.add(grade2Cnt);
                                gradeList.add(grade3Cnt);
                                gradeList.add(grade4Cnt);

                                // 미세먼지의 등급을 알아본다.
                                alertGrade(gradeList, csvList);

                            } catch (Exception e) {
                                log.error("에러 발생구간 => " + csvList);
                                log.error("에러 => 측정소별 농도 넣는 중 에러 발생 " + e);
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        } catch (IOException e) {
            log.error("에러 => csv 파일 찾는 중 에러 발생 " + e);
            e.printStackTrace();
        }
    }

    // 측정데이터를 넣기 전 중복 체크, 중복체크는 시, 영업소, 날짜를 기준으로 중복 체크를 진행한다.
    private boolean duplPartInfo(List<String> csvList) {

        int duplChk = partMapper.duplPartInfo(csvList);

        boolean result = false;

        if (duplChk != 0) {
            result = true;
        }

        return result;
    }

    // 미세먼지 등급을 알려주며, 등급을 저장한다.
    private List<String> alertGrade(List<Integer> gradeList, List<String> csvList) {

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

            log.info(csvList.get(0) + "시 " + csvList.get(2) + " 영업소는 현재 대기 등급 " + grade + " 입니다.");

            try {

                csvList.add(grade); // 리스트에 등급을 추가로 넣어준다.

                // 미세먼지 등급을 기록한다.
                partMapper.insertAlertInfo(csvList);

            } catch (Exception e) {
                log.error("에러 => 미세먼지 경보 중 에러 발생 " + e);
                e.printStackTrace();
            }
        }
        return csvList;
    }

}
