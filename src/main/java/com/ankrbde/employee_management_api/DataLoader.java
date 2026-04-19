package com.ankrbde.employee_management_api;

import com.ankrbde.employee_management_api.domain.Employee;
import com.ankrbde.employee_management_api.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DataLoader {

    @Bean
    CommandLineRunner init(EmployeeRepository repo) {
        return args -> {
            Employee e = new Employee();
            e.setName("Test User");
            e.setEmail("test@example.com");

            repo.save(e);

            log.info("Employee saved: {}", e.getId());
        };
    }
}
