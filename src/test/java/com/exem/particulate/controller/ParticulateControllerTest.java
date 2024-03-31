package com.exem.particulate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import com.exem.particulate.service.ParticulateService;

@ExtendWith(MockitoExtension.class)
public class ParticulateControllerTest {

    @Mock
    private ParticulateService particulateServiceMock;

    @InjectMocks
    private ParticulateController particulateController;

    @Test
    public void testParticulate() {
        // 예외가 발생하지 않을 때를 가정하여 테스트
        try {
            doNothing().when(particulateServiceMock).checkTable();
        
            doNothing().when(particulateServiceMock).particulate();

            particulateController.particulate();

            // 파트큘레이트 서비스의 checkTable 메서드가 호출되었는지 확인
            verify(particulateServiceMock, times(1)).checkTable();
            // 파트큘레이트 서비스의 particulate 메서드가 호출되었는지 확인
            verify(particulateServiceMock, times(1)).particulate();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testParticulate_Exception() {
        // 예외가 발생하는 상황을 가정하여 테스트
        try {
            doThrow(new RuntimeException("테스트 예외")).when(particulateServiceMock).checkTable();
            particulateController.particulate();

            // 파트큘레이트 서비스의 checkTable 메서드가 호출되었는지 확인
            verify(particulateServiceMock, times(1)).checkTable();
            // 파트큘레이트 서비스의 particulate 메서드가 호출되지 않았는지 확인
            verify(particulateServiceMock, never()).particulate();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
    }
}
