package com.budget.investments_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class InvestmentsServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(InvestmentsServiceApplication.class, args);
	}
}
