package com.ankrbde.employee_management_api.repository;

import com.ankrbde.employee_management_api.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByEmail(String email);

    Page<Employee> findByStatus(Employee.Status status, Pageable pageable);

    Page<Employee> findByStatusAndDepartmentId(
            Employee.Status status,
            UUID departmentId,
            Pageable pageable
    );

}