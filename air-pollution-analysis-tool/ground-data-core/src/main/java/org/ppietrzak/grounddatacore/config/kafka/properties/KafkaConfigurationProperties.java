package org.ppietrzak.grounddatacore.config.kafka.properties;

import lombok.Getter;
import lombok.Setter;
import org.ppietrzak.grounddatacore.infrastructure.kafka.KafkaTopics;
import org.ppietrzak.grounddatacore.infrastructure.kafka.exceptions.TopicConfigurationNotFoundException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConfigurationProperties(prefix = "configuration.kafka")
@Setter
public class KafkaConfigurationProperties {

    @Getter
    private String bootstrapAddress;

    @Getter
    private KafkaConsumerConfiguration consumer;
    private Map<String, KafkaTopicConfiguration> topics = new HashMap<>();

    public KafkaTopicConfiguration getTopicConfiguration(KafkaTopics topic) {
        return Optional.ofNullable(topics.get(topic.getEntryName()))
                .orElseThrow(() -> new TopicConfigurationNotFoundException(topic));
    }

    public Set<String> getBootstrapAddresses() {
        return Stream.of(bootstrapAddress.split(",")).collect(Collectors.toSet());
    }

    public String getBootstrapAddressesAsSingleString() {
        return bootstrapAddress;
    }
}
