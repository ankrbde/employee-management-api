package com.ankrbde.employee_management_api.config;

import com.ankrbde.employee_management_api.audit.error.NonRetryableException;
import com.ankrbde.employee_management_api.events.EmployeeEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, EmployeeEvent> consumerFactory() {

        JsonDeserializer<EmployeeEvent> deserializer =
                new JsonDeserializer<>(EmployeeEvent.class);

        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "audit-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmployeeEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, EmployeeEvent> consumerFactory,
            DefaultErrorHandler errorHandler
    ) {

        ConcurrentKafkaListenerContainerFactory<String, EmployeeEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, EmployeeEvent> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate,
                        (record, ex) ->
                                new TopicPartition("employee-events-dlq", record.partition())
                );

        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(10000L);
        backOff.setMaxElapsedTime(15000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // Do NOT retry non-retryable exceptions
        errorHandler.addNotRetryableExceptions(NonRetryableException.class);

        return errorHandler;
    }
}