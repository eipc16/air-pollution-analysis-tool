package org.ppietrzak.grounddatacore;

import org.ppietrzak.grounddatacore.config.indexation.cache.DataSourcesCacheConfigurationProperties;
import org.ppietrzak.grounddatacore.config.kafka.properties.KafkaConfigurationProperties;
import org.ppietrzak.grounddatacore.config.datasources.mongodb.properties.MongoConfigurationProperties;
import org.ppietrzak.grounddatacore.config.datasources.redis.properties.RedisConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		KafkaConfigurationProperties.class,
		MongoConfigurationProperties.class,
		RedisConfigurationProperties.class,
		DataSourcesCacheConfigurationProperties.class
})
public class GroundDataCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroundDataCoreApplication.class, args);
	}
}
