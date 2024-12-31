package com.mypro.particulate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.mypro.particulate")
public class ParticulateApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParticulateApplication.class, args);
	}

}
