package org.ppietrzak.dataprocessor.config.kafka.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.ppietrzak.dataprocessor.config.kafka.properties.KafkaConfigurationProperties;
import org.ppietrzak.dataprocessor.config.kafka.properties.KafkaConsumerConfiguration;
import org.ppietrzak.dataprocessor.domain.satellitemeasurements.boundary.SatelliteMeasurementIndexationBatchDTO;
import org.ppietrzak.grounddatacore.api.measurements.MeasurementsIndexationBatchDTO;
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
        return configs;
    }

    @Bean
    public ConsumerFactory<String, MeasurementsIndexationBatchDTO> groundMeasurementsConsumerFactory() {
        JsonDeserializer<MeasurementsIndexationBatchDTO> deserializer = new JsonDeserializer<>(MeasurementsIndexationBatchDTO.class, objectMapper.registerModule(new JavaTimeModule()));
        deserializer.addTrustedPackages("*");
        DefaultKafkaConsumerFactory<String, MeasurementsIndexationBatchDTO> factory = new DefaultKafkaConsumerFactory<>(
                consumerConfig(),
                new StringDeserializer(),
                deserializer
        );
        factory.setBootstrapServersSupplier(kafkaConfiguration::getBootstrapAddressesAsSingleString);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MeasurementsIndexationBatchDTO> groundMeasurementsKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MeasurementsIndexationBatchDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(groundMeasurementsConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, SatelliteMeasurementIndexationBatchDTO> satelliteMeasurementsConsumerFactory() {
        JsonDeserializer<SatelliteMeasurementIndexationBatchDTO> deserializer = new JsonDeserializer<>(SatelliteMeasurementIndexationBatchDTO.class, objectMapper.registerModule(new JavaTimeModule()));
        deserializer.addTrustedPackages("*");
        DefaultKafkaConsumerFactory<String, SatelliteMeasurementIndexationBatchDTO> factory = new DefaultKafkaConsumerFactory<>(
                consumerConfig(),
                new StringDeserializer(),
                deserializer
        );
        factory.setBootstrapServersSupplier(kafkaConfiguration::getBootstrapAddressesAsSingleString);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SatelliteMeasurementIndexationBatchDTO> satelliteMeasurementsKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SatelliteMeasurementIndexationBatchDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(satelliteMeasurementsConsumerFactory());
        return factory;
    }
}
