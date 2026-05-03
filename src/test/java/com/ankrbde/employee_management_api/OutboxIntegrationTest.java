package com.ankrbde.employee_management_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class OutboxIntegrationTest {

    @Test
    void endToEndFlowTest() {
        // 1. Create employee
        // 2. Verify outbox event created
        // 3. Wait for scheduler
        // 4. Verify Kafka message consumed
        // 5. Verify audit stored in Mongo
    }
}