package com.ankrbde.employee_management_api.events;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEvent {

    private String eventId;      // for idempotency
    private String eventType;
    private UUID employeeId;
    private String details;
}