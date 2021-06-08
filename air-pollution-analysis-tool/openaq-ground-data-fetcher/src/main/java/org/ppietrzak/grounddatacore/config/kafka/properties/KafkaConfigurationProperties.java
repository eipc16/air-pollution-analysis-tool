package org.ppietrzak.grounddatacore.config.kafka.properties;

import lombok.Getter;
import lombok.Setter;
import org.ppietrzak.grounddatacore.infrastructure.kafka.KafkaTopics;
import org.ppietrzak.grounddatacore.infrastructure.kafka.exceptions.TopicConfigurationNotFoundException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ConfigurationProperties(prefix = "configuration.kafka")
@Getter
@Setter
public class KafkaConfigurationProperties {

    private String bootstrapAddress;
    private Map<String, String> topics = new HashMap<>();

    public String getTopicName(KafkaTopics topic) {
        return Optional.ofNullable(topics.get(topic.getEntryName()))
                .orElseThrow(() -> new TopicConfigurationNotFoundException(topic));
    }
}
