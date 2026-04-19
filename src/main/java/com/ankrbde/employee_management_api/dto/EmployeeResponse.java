package com.ankrbde.employee_management_api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EmployeeResponse {

    private UUID id;
    private String name;
    private String email;
    private UUID departmentId;
    private UUID roleId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}