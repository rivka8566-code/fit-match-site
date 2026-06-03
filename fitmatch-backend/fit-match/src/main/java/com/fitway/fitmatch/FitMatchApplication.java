package com.fitway.fitmatch;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // מאפשר להריץ משימות מתוזמנות (כמו ניקוי תוכניות ישנות או שליחת התראות)
@SpringBootApplication
public class FitMatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitMatchApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
