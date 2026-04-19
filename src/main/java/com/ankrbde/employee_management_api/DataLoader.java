package com.ankrbde.employee_management_api;

import com.ankrbde.employee_management_api.domain.Employee;
import com.ankrbde.employee_management_api.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@Slf4j
public class DataLoader {

    @Bean
    CommandLineRunner init(EmployeeRepository repo) {
        return args -> {
            String randomEmail = "test-" + UUID.randomUUID() + "@example.com";

            Employee e = new Employee();
            e.setName("Test User");
            e.setEmail(randomEmail);

            repo.save(e);

            log.info("Employee saved with email: {}", randomEmail);
        };
    }
}
