package com.ankrbde.employee_management_api.outbox;

import com.ankrbde.employee_management_api.events.EmployeeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, EmployeeEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "employee-events";

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishEvents() {

        log.info("OUTBOX JOB RUNNING");

        List<OutboxEvent> events = outboxRepository.fetchUnprocessedBatch(50);

        if (events.isEmpty()) {
            return;
        }

        for (OutboxEvent event : events) {

            try {
                EmployeeEvent employeeEvent =
                        objectMapper.readValue(event.getPayload(), EmployeeEvent.class);

                log.info("Processing eventId={}", event.getEventId());

                kafkaTemplate.send(
                        TOPIC,
                        employeeEvent.eventId(),
                        employeeEvent
                );

                event.setProcessed(true);
                outboxRepository.save(event);

                log.info("OUTBOX → Kafka SUCCESS eventId={}", event.getEventId());

            } catch (Exception ex) {
                log.error("OUTBOX FAILED eventId={}", event.getEventId(), ex);
            }
        }
    }
}