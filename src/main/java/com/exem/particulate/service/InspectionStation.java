package com.exem.particulate.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.exem.particulate.mapper.ParticulateMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InspectionStation {

    private final Logger log = LoggerFactory.getLogger(AlertParticulate.class);
    private final ParticulateMapper partMapper;

    /*
     * 점검일이 있는지 확인 후 점검일이 있을 경우 정보 Insert
     * @ param csvList[0] = cityName
     * @ param csvList[1] = dateTimeStr
     * @ param csvList[2] = station
     * @ param csvList[3] = stationCode
     * @ param csvList[4] = particulate
     * @ param csvList[5] = fineParticualte
     */
    public List<String> insertInspection(List<String> csvList) throws Exception{

        String inspectionType = ""; 

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
            log.error("[에러] 점검일 DB 등록 중 에러 발생 => " + csvList);
            log.error("[에러] 내용 => " + e);
            e.printStackTrace();
        }

        return csvList;
    }
    
}
