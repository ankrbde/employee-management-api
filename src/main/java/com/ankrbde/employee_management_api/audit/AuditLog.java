package com.ankrbde.employee_management_api.audit;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    private String id;
    private String eventId;
    private UUID employeeId;
    private String action; // CREATE, UPDATE, DELETE
    private LocalDateTime timestamp;
    private String details;
}