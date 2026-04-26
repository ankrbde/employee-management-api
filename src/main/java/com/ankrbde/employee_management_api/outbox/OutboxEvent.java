package com.ankrbde.employee_management_api.outbox;

import com.ankrbde.employee_management_api.events.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    private String eventId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private UUID aggregateId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private Instant createdAt;

    private boolean processed;
}