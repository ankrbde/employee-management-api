package com.ankrbde.employee_management_api.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Slf4j
public class MongoDebugConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void check() {
        log.info("Mongo DB = {}", mongoTemplate.getDb().getName());
    }
}