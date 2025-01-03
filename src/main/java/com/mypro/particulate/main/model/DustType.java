package com.mypro.particulate.main.model;

/*
 * 2025-01-03 파일 생성
 * 
 * DUST_TYPE을 ENUM으로 변경
 */
public enum DustType {
    Fine("10"),
    Ultra("2.5");

    private final String description;

    DustType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
