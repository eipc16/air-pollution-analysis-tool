package org.ppietrzak.dataprocessor.infrastructure.kafka;

public enum KafkaTopics {
    GROUND_MEASUREMENTS_TOPIC("ground-measurements"),
    SATTELITE_MEASUREMENTS_TOPIC("satellite-measurements");

    private String entryName;

    KafkaTopics(String entryName) {
        this.entryName = entryName;
    }

    public String getName() {
        return entryName;
    }
}