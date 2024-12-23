package com.mypro.particulate.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DustModel {
    private String date;
    private String station;
    private int fineDust;
}