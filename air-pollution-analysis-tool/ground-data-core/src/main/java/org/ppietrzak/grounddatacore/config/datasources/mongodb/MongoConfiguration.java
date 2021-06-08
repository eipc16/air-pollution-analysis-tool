package org.ppietrzak.grounddatacore.config.datasources.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.ppietrzak.grounddatacore.config.datasources.mongodb.codecs.MongoOffsetDateTimeCodec;
import org.ppietrzak.grounddatacore.config.datasources.mongodb.properties.MongoConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableMongoRepositories(
        basePackages = {"org.ppietrzak"},
        includeFilters = {@ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {".*mongo.*", ".*MongoRepository"}
        )})
@RequiredArgsConstructor
@Slf4j
public class MongoConfiguration extends AbstractMongoClientConfiguration {

    private final MongoConfigurationProperties mongoConfigurationProperties;

    @Override
    protected String getDatabaseName() {
        return mongoConfigurationProperties.getDatabaseName();
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(createMongoClientSettings());
    }

    private MongoClientSettings createMongoClientSettings() {
        return MongoClientSettings.builder()
                .applyConnectionString(createConnectionString())
                .codecRegistry(getCompleteCodecRegistry())
                .build();
    }

    private CodecRegistry getCompleteCodecRegistry() {
        return CodecRegistries.fromRegistries(
                getCustomCodecRegistry(),
                MongoClientSettings.getDefaultCodecRegistry(),
                getPojoCodecRegistry()
        );
    }

    private CodecRegistry getCustomCodecRegistry() {
        return CodecRegistries.fromCodecs(
                new MongoOffsetDateTimeCodec()
        );
    }

    private CodecRegistry getPojoCodecRegistry() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                .automatic(true)
                .build();
        return CodecRegistries.fromProviders(pojoCodecProvider);
    }

    private ConnectionString createConnectionString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mongodb://");
        stringBuilder.append(String.format("%s:%s",
                mongoConfigurationProperties.getCredentials().getUsername(),
                String.copyValueOf(mongoConfigurationProperties.getCredentials().getPassword())));
        stringBuilder.append("@");
        stringBuilder.append(String.format("%s:%s",
                mongoConfigurationProperties.getHost(),
                mongoConfigurationProperties.getPort()));
        stringBuilder.append("/");
        stringBuilder.append(mongoConfigurationProperties.getDatabaseName());
        mongoConfigurationProperties.getAuthSource()
                .map(authSource -> String.format("?authSource=%s", authSource))
                .ifPresent(stringBuilder::append);
        return new ConnectionString(stringBuilder.toString());
    }

    @Override
    public boolean autoIndexCreation() {
        return mongoConfigurationProperties.isAutoCreateIndices();
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("org.ppietrzak");
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> convertes = Arrays.asList(MongoOffsetDateTimeCodec.getConverter());
        return new MongoCustomConversions(convertes);
    }
}