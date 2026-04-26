package com.ankrbde.employee_management_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@SpringBootApplication
@EnableScheduling
public class EmployeeManagementApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagementApiApplication.class, args);
	}

}
