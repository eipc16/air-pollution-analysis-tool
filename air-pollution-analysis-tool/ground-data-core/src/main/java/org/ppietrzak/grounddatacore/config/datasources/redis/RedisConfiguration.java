package org.ppietrzak.grounddatacore.config.datasources.redis;

import org.ppietrzak.grounddatacore.config.datasources.redis.converters.RedisOffsetDateTimeConverter;
import org.ppietrzak.grounddatacore.config.datasources.redis.properties.RedisConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.Optional;

@Configuration
@EnableRedisRepositories(
        basePackages = {"org.ppietrzak"},
        includeFilters = {@ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {".*redis.*", ".*CacheRepository", ".RedisRepository"}
        )})
public class RedisConfiguration {

    private final RedisConfigurationProperties redisConfigurationProperties;

    public RedisConfiguration(RedisConfigurationProperties redisConfigurationProperties) {
        this.redisConfigurationProperties = redisConfigurationProperties;
    }

    @Bean
    LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration connectionFactoryConfiguration = new RedisStandaloneConfiguration();
        connectionFactoryConfiguration.setHostName(redisConfigurationProperties.getHost());
        connectionFactoryConfiguration.setPort(redisConfigurationProperties.getPort());
        Optional.ofNullable(redisConfigurationProperties.getCredentials().getUsername())
                .ifPresent(connectionFactoryConfiguration::setUsername);
        connectionFactoryConfiguration.setPassword(redisConfigurationProperties.getCredentials().getPassword());
        return new LettuceConnectionFactory(connectionFactoryConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }

    @Bean
    public RedisCustomConversions redisCustomConversions(RedisOffsetDateTimeConverter offsetDateTimeConverter) {
        return new RedisCustomConversions(offsetDateTimeConverter.getConverters());
    }
}
