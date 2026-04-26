package com.ankrbde.employee_management_api.outbox;

import com.ankrbde.employee_management_api.events.EmployeeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, EmployeeEvent> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutboxEvent> events = outboxRepository.findByProcessedFalse();

        for (OutboxEvent event : events) {
            try {
                // 1. Convert payload → EmployeeEvent
                EmployeeEvent employeeEvent =
                        objectMapper.readValue(event.getPayload(), EmployeeEvent.class);

                kafkaTemplate.send(
                        "employee-events",
                        employeeEvent.eventId(),
                        employeeEvent
                );

                // 3. Mark as processed ONLY AFTER SUCCESS
                event.setProcessed(true);
                outboxRepository.save(event);

                log.info("OUTBOX → Kafka SUCCESS eventId={}", event.getEventId());

            } catch (Exception ex) {
                // DO NOT mark processed → retry will happen
                log.error("OUTBOX → Kafka FAILED eventId={}", event.getEventId(), ex);
            }
        }
    }
}