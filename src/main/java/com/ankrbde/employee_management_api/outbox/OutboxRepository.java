package com.ankrbde.employee_management_api.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, String> {

    List<OutboxEvent> findByProcessedFalse();
}