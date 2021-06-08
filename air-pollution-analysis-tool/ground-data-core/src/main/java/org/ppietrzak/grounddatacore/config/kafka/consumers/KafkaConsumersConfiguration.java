package org.ppietrzak.grounddatacore.config.kafka.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceDTO;
import org.ppietrzak.grounddatacore.api.stations.GroundStationIndexationBatchDTO;
import org.ppietrzak.grounddatacore.config.kafka.properties.KafkaConfigurationProperties;
import org.ppietrzak.grounddatacore.config.kafka.properties.KafkaConsumerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@AllArgsConstructor
public class KafkaConsumersConfiguration {

    private final ObjectMapper objectMapper;
    private final KafkaConfigurationProperties kafkaConfiguration;

    public Map<String, Object> consumerConfig() {
        KafkaConsumerConfiguration consumerConfiguration = kafkaConfiguration.getConsumer();
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getBootstrapAddresses());
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, consumerConfiguration.getGroupId());
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerConfiguration.getAutoOffsetReset());
//        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "org.ppietrzak.grounddatacore.*");
        return configs;
    }

    @Bean
    public ConsumerFactory<String, GroundDataSourceDTO> groundDataSourceConsumerFactory() {
        JsonDeserializer<GroundDataSourceDTO> deserializer = new JsonDeserializer<>(GroundDataSourceDTO.class, objectMapper.registerModule(new JavaTimeModule()));
        deserializer.addTrustedPackages("*");
        DefaultKafkaConsumerFactory<String, GroundDataSourceDTO> factory = new DefaultKafkaConsumerFactory<>(
                consumerConfig(),
                new StringDeserializer(),
                deserializer
        );
        factory.setBootstrapServersSupplier(kafkaConfiguration::getBootstrapAddressesAsSingleString);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GroundDataSourceDTO> groundDataKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GroundDataSourceDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(groundDataSourceConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, GroundStationIndexationBatchDTO> groundStationsConsumerFactory() {
        JsonDeserializer<GroundStationIndexationBatchDTO> deserializer = new JsonDeserializer<>(GroundStationIndexationBatchDTO.class, objectMapper);
        deserializer.addTrustedPackages("*");
        DefaultKafkaConsumerFactory<String, GroundStationIndexationBatchDTO> factory = new DefaultKafkaConsumerFactory<>(
                consumerConfig(),
                new StringDeserializer(),
                deserializer
        );
        factory.setBootstrapServersSupplier(kafkaConfiguration::getBootstrapAddressesAsSingleString);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GroundStationIndexationBatchDTO> groundStationsKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GroundStationIndexationBatchDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(groundStationsConsumerFactory());
        return factory;
    }
}
