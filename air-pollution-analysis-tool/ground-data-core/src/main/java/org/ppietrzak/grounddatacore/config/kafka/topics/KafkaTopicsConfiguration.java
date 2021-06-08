package org.ppietrzak.grounddatacore.config.kafka.topics;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.ppietrzak.grounddatacore.infrastructure.kafka.KafkaTopics;
import org.ppietrzak.grounddatacore.config.kafka.properties.KafkaConfigurationProperties;
import org.ppietrzak.grounddatacore.config.kafka.properties.KafkaTopicConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class KafkaTopicsConfiguration {

    private final KafkaConfigurationProperties kafkaConfiguration;

    @Bean
    public NewTopic sourceTopic() {
        KafkaTopicConfiguration sourceTopicConfiguration = kafkaConfiguration.getTopicConfiguration(KafkaTopics.SOURCE_TOPIC);
        return createNewTopic(sourceTopicConfiguration);
    }

    private NewTopic createNewTopic(KafkaTopicConfiguration config) {
        return new NewTopic(config.getName(), config.getPartitions(), config.getReplicationFactor());
    }

    @Bean
    public NewTopic stationsTopic() {
        KafkaTopicConfiguration config = kafkaConfiguration.getTopicConfiguration(KafkaTopics.STATIONS_TOPIC);
        return createNewTopic(config);
    }
}
