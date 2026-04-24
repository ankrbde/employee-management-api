package com.ankrbde.employee_management_api.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "employee-events";

    public void publish(String eventType, UUID employeeId, String details) {

        EmployeeEvent event = EmployeeEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .employeeId(employeeId)
                .details(details)
                .build();

        kafkaTemplate.send(TOPIC, event);

        log.info("Published eventId={} type={}", event.getEventId(), event.getEventType());
    }
}