package com.ankrbde.employee_management_api.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "employee-events";

    public void publish(String eventType, UUID employeeId, String details) {

        String message = buildMessage(eventType, employeeId, details);

        kafkaTemplate.send(TOPIC, message);

        log.info("Published Kafka event: {}", message);
    }

    private String buildMessage(String eventType, UUID employeeId, String details) {
        return "{"
                + "\"eventType\":\"" + eventType + "\","
                + "\"employeeId\":\"" + employeeId + "\","
                + "\"details\":\"" + details + "\""
                + "}";
    }
}