package org.ppietrzak.grounddatacore.config.kafka.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaTopicConfiguration {

    private String name;
    private int partitions = 1;
    private short replicationFactor = 1;
}
