package org.ppietrzak.grounddatacore.config.kafka.producers;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsIndexationBatchDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfiguration {

    private final DefaultProducerConfiguration defaultProducerConfiguration;

    @Bean
    public ProducerFactory<String, MeasurementsIndexationBatchDTO> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
                defaultProducerConfiguration.getProducerFactoryConfiguration(),
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public KafkaTemplate<String, MeasurementsIndexationBatchDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
