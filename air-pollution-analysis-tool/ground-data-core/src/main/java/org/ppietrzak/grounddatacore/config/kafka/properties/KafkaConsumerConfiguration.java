package org.ppietrzak.grounddatacore.config.kafka.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaConsumerConfiguration {

    private String autoOffsetReset;
    private String groupId;
}
