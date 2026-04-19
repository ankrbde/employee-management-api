package com.ankrbde.employee_management_api.mapper;

import com.ankrbde.employee_management_api.domain.Employee;
import com.ankrbde.employee_management_api.dto.EmployeeResponse;

public class EmployeeMapper {

    public static EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .email(e.getEmail())
                .departmentId(e.getDepartmentId())
                .roleId(e.getRoleId())
                .status(e.getStatus().name())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}