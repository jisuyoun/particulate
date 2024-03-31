package com.exem.particulate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exem.particulate.mapper.ParticulateMapper;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YourServiceImplTest {

    @Mock
    ParticulateMapper partMapper;

    @InjectMocks
    ParticulateServiceImpl particulateServiceImpl;

    @Test
    void checkTable() throws Exception {
        // Mock 객체의 동작 설정
        when(partMapper.checkTable()).thenReturn(0);

        // 테스트할 메서드 실행
        particulateServiceImpl.checkTable();

        // Mock 객체의 특정 메서드 호출 검증
        verify(partMapper, times(1)).createCityTable();
        verify(partMapper, times(1)).createStationTable();
        verify(partMapper, times(1)).createParticulateTable();
        verify(partMapper, times(1)).createAlertTable();
        verify(partMapper, times(1)).createInspectionTable();
        verify(partMapper, times(1)).insertCityInfo(anyList());
        verify(partMapper, times(1)).insertStationInfo(anyList());
    }
}
