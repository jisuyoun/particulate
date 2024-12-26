package com.mypro.particulate.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DustModel {
    private String date;
    private String time;
    private String station;
    private int fineDust;
    private int UltraFineDust;
}