package io.microhooks.eda.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.microhooks.eda.EventProducer;
import io.microhooks.eda.providers.kafka.KafkaEventProducer;
import io.microhooks.eda.providers.rabbitmq.RabbitMQEventProducer;

@Configuration
public class EventProducerConfig {

    @Value("${io.microhooks.providers.broker.type:kafka}")
    private String brokerType;

    @Value("${io.microhooks.providers.broker.cluster}")
    protected String brokers;
    
    @Bean
    public <T, U> EventProducer<T, U> eventProducer() {        
        if (brokerType.trim().equals("kafka")) {
            return new KafkaEventProducer<>(brokers);
        }
        if (brokerType.trim().equals("rabbitmq")) {
            return new RabbitMQEventProducer<>(brokers);
        }
        throw new BrokerNotSupportedException(brokerType);
    }
}