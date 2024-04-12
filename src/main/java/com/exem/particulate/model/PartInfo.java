package com.exem.particulate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartInfo {
    
    private String city;

    private String station;

    private int particulate;

    private int fineParticulate;

    private String regDt;

    // 생성자
    public PartInfo(String city, String regDt, String station) {
        this.city = city;
        this.regDt = regDt;
        this.station = station;
    }

    public PartInfo(String city, String regDt, String station, int particulate, int fineParticulate) {
        this.city = city;
        this.regDt = regDt;
        this.station = station;
        this.particulate = particulate;
        this.fineParticulate = fineParticulate;
    }

}
