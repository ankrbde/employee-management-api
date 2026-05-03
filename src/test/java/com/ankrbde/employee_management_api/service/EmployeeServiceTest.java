package com.ankrbde.employee_management_api.service;

import com.ankrbde.employee_management_api.domain.Employee;
import com.ankrbde.employee_management_api.dto.EmployeeRequest;
import com.ankrbde.employee_management_api.exception.DuplicateResourceException;
import com.ankrbde.employee_management_api.repository.EmployeeRepository;
import com.ankrbde.employee_management_api.outbox.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void shouldCreateEmployee() {

        EmployeeRequest request = new EmployeeRequest();
        request.setName("John");
        request.setEmail("john@test.com");
        request.setDepartmentId(UUID.randomUUID());
        request.setRoleId(UUID.randomUUID());

        when(employeeRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setEmail("john@test.com");
        employee.setName("John");
        employee.setStatus(Employee.Status.ACTIVE);

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(employee);

        EmployeeService serviceSpy = spy(employeeService);

        var result = serviceSpy.createEmployee(request);

        assertNotNull(result);
        assertEquals("john@test.com", result.getEmail());

        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(outboxRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {

        EmployeeRequest request = new EmployeeRequest();
        request.setEmail("duplicate@test.com");

        when(employeeRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new Employee()));

        assertThrows(DuplicateResourceException.class,
                () -> employeeService.createEmployee(request));
    }

    @Test
    void shouldReturnEmployeeById() {

        UUID id = UUID.randomUUID();

        Employee employee = new Employee();
        employee.setId(id);
        employee.setEmail("test@test.com");
        employee.setStatus(Employee.Status.ACTIVE);

        when(employeeRepository.findById(id))
                .thenReturn(Optional.of(employee));

        var result = employeeService.getEmployee(id);

        assertEquals("test@test.com", result.getEmail());
    }
}
