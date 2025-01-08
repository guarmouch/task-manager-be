package com.taskmanager.ts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication@EnableJpaAuditing
public class TsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TsApplication.class, args);
	}

}
