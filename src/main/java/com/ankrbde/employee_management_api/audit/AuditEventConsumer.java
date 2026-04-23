package com.ankrbde.employee_management_api.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEventConsumer {

    private final AuditLogRepository repository;

    @KafkaListener(topics = "employee-events", groupId = "audit-group")
    public void consume(String message) {

        log.info("Kafka event received: {}", message);

        AuditLog auditLog = AuditLog.builder()
                .details(message)
                .timestamp(LocalDateTime.now())
                .build();

        repository.save(auditLog);

        log.info("Audit log saved from Kafka event");
    }
}