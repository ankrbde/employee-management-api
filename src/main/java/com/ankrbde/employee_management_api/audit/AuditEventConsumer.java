package com.ankrbde.employee_management_api.audit;

import com.ankrbde.employee_management_api.events.EmployeeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEventConsumer {

    private final AuditLogRepository repository;

    @KafkaListener(topics = "employee-events", groupId = "audit-group")
    public void consume(EmployeeEvent event,
                        @Header(value = "correlationId", required = false) String correlationId) {

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
            log.warn("No correlationId header on eventId={}, generated fallback={}",
                    event.eventId(), correlationId);
        }

        MDC.put("correlationId", correlationId);
        try {
            log.info("Received eventId={} type={} correlationId={}", event.eventId(), event.eventType(), correlationId);

            AuditLog auditLog = AuditLog.builder()
                    .eventId(event.eventId())
                    .employeeId(event.employeeId())
                    .action(event.eventType().name())
                    .details(event.details())
                    .timestamp(LocalDateTime.now())
                    .correlationId(correlationId)
                    .build();
            try {
                repository.save(auditLog);
            } catch (DuplicateKeyException e) {
                log.warn("Duplicate record detected: {}", e.getMessage());
            }
        } finally {
            MDC.remove("correlationId");
        }
    }
}