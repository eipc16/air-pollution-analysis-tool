package org.ppietrzak.grounddatacore.infrastructure.kafka.exceptions;

import org.ppietrzak.grounddatacore.infrastructure.kafka.KafkaTopics;

import java.text.MessageFormat;

public class TopicConfigurationNotFoundException extends RuntimeException {

    private String topicName;

    private TopicConfigurationNotFoundException() {
        // empty
    }

    public TopicConfigurationNotFoundException(KafkaTopics topic) {
        super(MessageFormat.format("Configuration for topic: {0} not found", topic.getEntryName()));
        this.topicName = topic.getEntryName();
    }
}
