package com.exem.particulate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.exem.particulate.controller.ParticulateController;

@SpringBootApplication
public class ParticulateApplication {

	public static void main(String[] args) {
		//SpringApplication.run(ParticulateApplication.class, args);

        ConfigurableApplicationContext context = SpringApplication.run(ParticulateApplication.class, args);

        // csv를 읽고, 데이터 처리 메소드 실행
        ParticulateController particulateController = context.getBean(ParticulateController.class);
        particulateController.particulate();
	}

}
