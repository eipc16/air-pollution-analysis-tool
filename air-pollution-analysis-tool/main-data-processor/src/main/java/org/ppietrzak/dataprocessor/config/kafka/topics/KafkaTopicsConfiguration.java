package org.ppietrzak.dataprocessor.config.kafka.topics;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.ppietrzak.dataprocessor.config.kafka.properties.KafkaConfigurationProperties;
import org.ppietrzak.dataprocessor.config.kafka.properties.KafkaTopicConfiguration;
import org.ppietrzak.dataprocessor.infrastructure.kafka.KafkaTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class KafkaTopicsConfiguration {

    private final KafkaConfigurationProperties kafkaConfiguration;

    @Bean
    public NewTopic groundMeasurementsTopic() {
        KafkaTopicConfiguration groundMeasurementsTopic =
                kafkaConfiguration.getTopicConfiguration(KafkaTopics.GROUND_MEASUREMENTS_TOPIC);
        return createNewTopic(groundMeasurementsTopic);
    }

    private NewTopic createNewTopic(KafkaTopicConfiguration config) {
        return new NewTopic(config.getName(), config.getPartitions(), config.getReplicationFactor());
    }

    @Bean
    public NewTopic satelliteMeasurementsTopic() {
        KafkaTopicConfiguration satelliteMeasurementsTopic =
                kafkaConfiguration.getTopicConfiguration(KafkaTopics.SATTELITE_MEASUREMENTS_TOPIC);
        return createNewTopic(satelliteMeasurementsTopic);
    }
}
