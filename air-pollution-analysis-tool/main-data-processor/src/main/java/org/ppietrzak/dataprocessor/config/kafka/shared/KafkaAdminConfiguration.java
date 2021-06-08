package org.ppietrzak.dataprocessor.config.kafka.shared;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.ppietrzak.dataprocessor.config.kafka.properties.KafkaConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Collections;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfiguration {

    private final KafkaConfigurationProperties kafkaConfiguration;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(getKafkaAdminConfiguration());
    }

    private Map<String, Object> getKafkaAdminConfiguration() {
        return Collections.singletonMap(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getBootstrapAddressesAsSingleString());
    }
}
