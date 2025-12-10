package com.devekoc.camerAtlas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CamerAtlasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamerAtlasApplication.class, args);
	}

}
