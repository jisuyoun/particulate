package com.mypro.particulate.main.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.stereotype.Service;

import com.mypro.particulate.main.model.DustModel;

@Service
public class DustService {
    private static final int DUST_THRESHOLD = 50; // 미세먼지 기준치

    public List<DustModel> readDustData(String csvFilePath) throws IOException {
        List<DustModel> dustModelList = new ArrayList<>();
        
        String encoding = detectFileEncoding(csvFilePath);
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilePath), encoding))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Reader reader = new FileReader(csvFilePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);
            for (CSVRecord record : records) {
                String date = record.get("날짜");
                String station = record.get("측정소명");
                int fineDust = Integer.parseInt(record.get("PM10"));
                //dustModelList.add(new DustModel(date, station, fineDust));
            }
        }
        return dustModelList;
    }

    // 파일 인코딩 감지
    public static String detectFileEncoding(String csvFilePath) throws IOException {
        FileInputStream fis = null;
        String detectedCharset = "";
        try {
            byte[] buf = new byte[4096];
            fis = new FileInputStream(csvFilePath);
            
            UniversalDetector detector = new UniversalDetector(null);

            int nread;
            while((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();

            detectedCharset = detector.getDetectedCharset();
            if (detectedCharset != null && detector.isDone() && Charset.isSupported(detectedCharset)) {
                detector.reset();
                return detectedCharset;
            }

        } catch (Exception e) {
            return "";
        } finally {
            try { if(fis != null) fis.close();} catch (Exception e) {}
        }
        return detectedCharset;
    }

    public boolean isDustAboveThreshold(DustModel dustModel) {
        return dustModel.getFineDust() > DUST_THRESHOLD;
    }
}
