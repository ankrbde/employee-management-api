package com.ankrbde.employee_management_api.service;

import com.ankrbde.employee_management_api.domain.Employee;
import com.ankrbde.employee_management_api.dto.EmployeeRequest;
import com.ankrbde.employee_management_api.dto.EmployeeResponse;
import com.ankrbde.employee_management_api.events.EmployeeEvent;
import com.ankrbde.employee_management_api.events.EventType;
import com.ankrbde.employee_management_api.exception.DuplicateResourceException;
import com.ankrbde.employee_management_api.exception.ResourceNotFoundException;
import com.ankrbde.employee_management_api.mapper.EmployeeMapper;
import com.ankrbde.employee_management_api.outbox.OutboxEvent;
import com.ankrbde.employee_management_api.outbox.OutboxRepository;
import com.ankrbde.employee_management_api.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository repository;
    private final OutboxRepository outboxRepository;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        repository.findByEmail(request.getEmail())
                .ifPresent(e -> {
                    throw new DuplicateResourceException("Email already exists");
                });

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartmentId(request.getDepartmentId());
        employee.setRoleId(request.getRoleId());

        Employee saved = repository.save(employee);

        saveOutboxEvent(EventType.CREATE, saved.getId(), "Employee created");

        return EmployeeMapper.toResponse(saved);
    }

    @Cacheable(value = "employees", key = "#id")
    public EmployeeResponse getEmployee(UUID id) {

        Employee employee = repository.findById(id)
                .filter(e -> e.getStatus() == Employee.Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        return EmployeeMapper.toResponse(employee);
    }

    public Page<EmployeeResponse> getEmployees(int page, int size, UUID departmentId) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Employee> employees;

        if (departmentId != null) {
            employees = repository.findByStatusAndDepartmentId(
                    Employee.Status.ACTIVE,
                    departmentId,
                    pageable
            );
        } else {
            employees = repository.findByStatus(
                    Employee.Status.ACTIVE,
                    pageable
            );
        }

        return employees.map(EmployeeMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request) {

        Employee employee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!employee.getEmail().equals(request.getEmail())) {
            repository.findByEmail(request.getEmail())
                    .ifPresent(e -> {
                        throw new DuplicateResourceException("Email already exists");
                    });
        }

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartmentId(request.getDepartmentId());
        employee.setRoleId(request.getRoleId());

        Employee updated = repository.save(employee);

        saveOutboxEvent(EventType.UPDATE, id, "Employee updated");

        return EmployeeMapper.toResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "employees", key = "#id")
    public void deleteEmployee(UUID id) {

        Employee employee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        employee.setStatus(Employee.Status.INACTIVE);
        repository.save(employee);

        saveOutboxEvent(EventType.DELETE, id, "Employee soft deleted");
    }

    private void saveOutboxEvent(EventType eventType, UUID employeeId, String details) {

        log.info("OUTBOX JOB RUNNING");

        EmployeeEvent event = new EmployeeEvent(
                UUID.randomUUID().toString(),
                eventType,
                employeeId,
                details
        );

        try {
            String payload = new ObjectMapper().writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(event.eventId())
                    .eventType(event.eventType())
                    .aggregateId(employeeId)
                    .payload(payload)
                    .createdAt(Instant.now())
                    .processed(false)
                    .build();

            outboxRepository.save(outboxEvent);

            log.info("Outbox event saved eventId={} type={}", event.eventId(), event.eventType());

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}