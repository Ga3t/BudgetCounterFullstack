package com.budget.leger_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.budget")
@EnableDiscoveryClient
public class LegerServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(LegerServiceApplication.class, args);
	}

}
