package com.ankrbde.employee_management_api.outbox;

import com.ankrbde.employee_management_api.events.EmployeeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class OutboxPublisherTest {

    @Test
    void shouldPublishEventToKafka() throws Exception {

        OutboxRepository repo = mock(OutboxRepository.class);
        KafkaTemplate<String, EmployeeEvent> kafkaTemplate =
                mock(KafkaTemplate.class);
        ObjectMapper mapper = new ObjectMapper();

        OutboxPublisher publisher =
                new OutboxPublisher(repo, kafkaTemplate, mapper);

        OutboxEvent event = OutboxEvent.builder()
                .eventId("1")
                .aggregateId(UUID.randomUUID())
                .payload("{\"eventId\":\"1\"}")
                .processed(false)
                .build();

        when(repo.fetchUnprocessedBatch(50)).thenReturn(List.of(event));

        publisher.publishEvents();

        verify(kafkaTemplate, times(1)).send(any(), any(), any());
        verify(repo, times(1)).save(event);
    }
}