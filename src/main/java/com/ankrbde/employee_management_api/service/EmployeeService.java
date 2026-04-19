package com.ankrbde.employee_management_api.service;

import com.ankrbde.employee_management_api.domain.Employee;
import com.ankrbde.employee_management_api.dto.EmployeeRequest;
import com.ankrbde.employee_management_api.dto.EmployeeResponse;
import com.ankrbde.employee_management_api.mapper.EmployeeMapper;
import com.ankrbde.employee_management_api.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeResponse createEmployee(EmployeeRequest request) {

        repository.findByEmail(request.getEmail())
                .ifPresent(e -> {
                    throw new RuntimeException("Email already exists");
                });

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartmentId(request.getDepartmentId());
        employee.setRoleId(request.getRoleId());

        Employee saved = repository.save(employee);

        log.info("Created employee with id={}", saved.getId());

        return EmployeeMapper.toResponse(saved);
    }

    public EmployeeResponse getEmployee(UUID id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return EmployeeMapper.toResponse(employee);
    }
}