package com.ankrbde.employee_management_api.repository;

import com.ankrbde.employee_management_api.domain.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    @Test
    void shouldSaveAndFetchEmployee() {
        Employee employee = Employee.builder()
                .id(UUID.randomUUID())
                .email("repo@test.com")
                .build();

        repository.save(employee);

        Optional<Employee> result = repository.findById(employee.getId());

        assertTrue(result.isPresent());
        assertEquals("repo@test.com", result.get().getEmail());
    }
}