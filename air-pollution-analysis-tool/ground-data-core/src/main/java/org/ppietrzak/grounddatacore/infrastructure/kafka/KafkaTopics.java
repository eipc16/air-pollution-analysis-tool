package org.ppietrzak.grounddatacore.infrastructure.kafka;

public enum KafkaTopics {
    SOURCE_TOPIC("sourceTopic"),
    STATIONS_TOPIC("stationsTopic");

    private String entryName;

    KafkaTopics(String entryName) {
        this.entryName = entryName;
    }

    public String getEntryName() {
        return entryName;
    }
}
