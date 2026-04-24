package com.ankrbde.employee_management_api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic employeeEvents() {
        return new NewTopic("employee-events", 1, (short) 1);
    }

    @Bean
    public NewTopic employeeEventsDlq() {
        return new NewTopic("employee-events-dlq", 1, (short) 1);
    }
}