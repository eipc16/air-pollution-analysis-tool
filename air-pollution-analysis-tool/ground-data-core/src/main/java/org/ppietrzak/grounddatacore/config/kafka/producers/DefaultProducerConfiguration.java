package org.ppietrzak.grounddatacore.config.kafka.producers;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.ppietrzak.grounddatacore.config.kafka.properties.KafkaConfigurationProperties;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.HashMap;
import java.util.Map;

@Component
@ApplicationScope
@RequiredArgsConstructor
public class DefaultProducerConfiguration {

    private final KafkaConfigurationProperties kafkaConfigurationProperties;

    Map<String, Object> getProducerFactoryConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigurationProperties.getBootstrapAddress());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
//        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "41943040");
        return config;
    }
}
