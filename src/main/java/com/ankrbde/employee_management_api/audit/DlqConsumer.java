package com.ankrbde.employee_management_api.audit;

import com.ankrbde.employee_management_api.events.EmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DlqConsumer {

    @KafkaListener(topics = "employee-events-dlq", groupId = "dlq-group")
    public void consume(EmployeeEvent event) {
        log.error("DLQ EVENT eventId={} type={}",
                event.eventId(),
                event.eventType()
        );
    }
}