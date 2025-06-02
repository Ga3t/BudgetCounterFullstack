package com.budget.AuhtService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.budget.AuhtService", "com.budget.core"})
@EnableDiscoveryClient
public class AuhtServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuhtServiceApplication.class, args);
	}
}
