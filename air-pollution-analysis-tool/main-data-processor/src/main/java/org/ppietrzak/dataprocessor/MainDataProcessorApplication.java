package org.ppietrzak.dataprocessor;

import org.ppietrzak.dataprocessor.config.datasources.mongodb.properties.MongoConfigurationProperties;
import org.ppietrzak.dataprocessor.config.kafka.properties.KafkaConfigurationProperties;
import org.ppietrzak.dataprocessor.config.services.ServicesConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({
        KafkaConfigurationProperties.class,
        MongoConfigurationProperties.class,
        ServicesConfigurationProperties.class
})
@EnableScheduling
public class MainDataProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainDataProcessorApplication.class, args);
    }
}