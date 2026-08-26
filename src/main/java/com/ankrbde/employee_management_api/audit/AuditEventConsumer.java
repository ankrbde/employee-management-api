package com.ankrbde.employee_management_api.audit;

import com.ankrbde.employee_management_api.events.EmployeeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEventConsumer {

    private final AuditLogRepository repository;

    @KafkaListener(topics = "employee-events", groupId = "audit-group")
    public void consume(EmployeeEvent event) {

        log.info("Received eventId={} type={}", event.eventId(), event.eventType());

        AuditLog auditLog = AuditLog.builder()
                .eventId(event.eventId())
                .employeeId(event.employeeId())
                .action(event.eventType().name())
                .details(event.details())
                .timestamp(LocalDateTime.now())
                .build();
        try {
            repository.save(auditLog);
        } catch (DuplicateKeyException e) {
            log.warn("Duplicate record detected: {}", e.getMessage());
        }

        log.info("Audit saved for eventId={}", event.eventId());
    }
}