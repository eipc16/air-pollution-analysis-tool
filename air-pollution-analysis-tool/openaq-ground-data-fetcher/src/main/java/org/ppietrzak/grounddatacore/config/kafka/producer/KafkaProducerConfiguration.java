package org.ppietrzak.grounddatacore.config.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceDTO;
import org.ppietrzak.grounddatacore.api.stations.GroundStationIndexationBatchDTO;
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
    public ProducerFactory<String, GroundDataSourceDTO> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
                defaultProducerConfiguration.getProducerFactoryConfiguration(),
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public KafkaTemplate<String, GroundDataSourceDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, GroundStationIndexationBatchDTO> producerFactoryGroundStationIndexationBatch() {
        return new DefaultKafkaProducerFactory<>(
                defaultProducerConfiguration.getProducerFactoryConfiguration(),
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public KafkaTemplate<String, GroundStationIndexationBatchDTO> kafkaTemplateGroundStationIndexationBatch() {
        return new KafkaTemplate<>(producerFactoryGroundStationIndexationBatch());
    }
}
