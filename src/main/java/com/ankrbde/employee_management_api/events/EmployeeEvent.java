package com.ankrbde.employee_management_api.events;

import java.util.UUID;

public record EmployeeEvent(
        String eventId,
        EventType eventType,
        UUID employeeId,
        String details
) {}