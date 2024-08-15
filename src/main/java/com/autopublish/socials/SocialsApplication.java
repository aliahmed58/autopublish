package com.autopublish.socials;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@EnableAsync
public class SocialsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialsApplication.class, args);
	}



}
