package com.ankrbde.employee_management_api.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository repository;

    public void log(UUID employeeId, String action, String details) {

        try {
            AuditLog auditLog = AuditLog.builder()
                    .employeeId(employeeId)
                    .action(action)
                    .timestamp(LocalDateTime.now())
                    .details(details)
                    .build();

            repository.save(auditLog);

            log.info("Audit log saved for employeeId={} action={}", employeeId, action);

        } catch (Exception ex) {
            log.error(
                    "Failed to save audit log for employeeId={} action={} error={}",
                    employeeId,
                    action,
                    ex.getMessage(),
                    ex
            );
        }
    }
}