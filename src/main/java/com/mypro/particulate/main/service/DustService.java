package com.mypro.particulate.main.service;

import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.mypro.particulate.main.model.DustModel;

public class DustService {
    private static final int DUST_THRESHOLD = 50; // 미세먼지 기준치

    public List<DustModel> readDustData(String csvFilePath) throws IOException {
        List<DustModel> dustModelList = new ArrayList<>();
        try (Reader reader = new FileReader(csvFilePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);
            for (CSVRecord record : records) {
                String date = record.get("날짜");
                String station = record.get("측정소명");
                int fineDust = Integer.parseInt(record.get("PM10"));
                dustModelList.add(new DustModel(date, station, fineDust));
            }
        }
        return dustModelList;
    }

    public boolean isDustAboveThreshold(DustModel dustModel) {
        return dustModel.getFineDust() > DUST_THRESHOLD;
    }
}
