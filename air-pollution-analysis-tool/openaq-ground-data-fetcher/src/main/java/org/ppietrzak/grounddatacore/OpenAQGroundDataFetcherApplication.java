package org.ppietrzak.grounddatacore;

import org.ppietrzak.grounddatacore.config.kafka.properties.KafkaConfigurationProperties;
import org.ppietrzak.grounddatacore.config.openaq.OpenAQConfigurationProperties;
import org.ppietrzak.grounddatacore.config.source.SourceConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		KafkaConfigurationProperties.class,
		SourceConfigurationProperties.class,
		OpenAQConfigurationProperties.class
})
public class OpenAQGroundDataFetcherApplication {
	public static void main(String[] args) {
		SpringApplication.run(OpenAQGroundDataFetcherApplication.class, args);
	}
}
