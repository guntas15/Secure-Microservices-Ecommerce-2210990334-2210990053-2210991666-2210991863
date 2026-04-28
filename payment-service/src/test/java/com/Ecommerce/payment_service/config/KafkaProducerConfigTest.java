package com.Ecommerce.payment_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaProducerConfigTest {

    @Test
    void producerFactoryBean_shouldBeCreated() {

        KafkaProducerConfig config = new KafkaProducerConfig();

        ProducerFactory<String, Object> producerFactory =
                config.producerFactory();

        assertThat(producerFactory).isNotNull();
    }

    @Test
    void kafkaTemplateBean_shouldBeCreated() {

        KafkaProducerConfig config = new KafkaProducerConfig();

        ProducerFactory<String, Object> producerFactory =
                config.producerFactory();

        KafkaTemplate<String, Object> kafkaTemplate =
                config.kafkaTemplate(producerFactory);

        assertThat(kafkaTemplate).isNotNull();
    }
}
