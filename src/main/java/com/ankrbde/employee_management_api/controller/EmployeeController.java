package com.ankrbde.employee_management_api.controller;

import com.ankrbde.employee_management_api.dto.EmployeeRequest;
import com.ankrbde.employee_management_api.dto.EmployeeResponse;
import com.ankrbde.employee_management_api.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @PostMapping
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
        return service.createEmployee(request);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable UUID id) {
        return service.getEmployee(id);
    }
}