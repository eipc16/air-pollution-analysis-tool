package org.ppietrzak.grounddatacore.initialization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ppietrzak.grounddatacore.api.sources.GroundDataSourceDTO;
import org.ppietrzak.grounddatacore.config.kafka.properties.KafkaConfigurationProperties;
import org.ppietrzak.grounddatacore.config.source.SourceConfigurationProperties;
import org.ppietrzak.grounddatacore.infrastructure.kafka.KafkaTopics;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.*;
import java.util.Collections;

@Component
@Slf4j
@ApplicationScope
@RequiredArgsConstructor
public class OpenAQSourceRegistrationInitializer implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final SourceConfigurationProperties sourceConfigurationProperties;
    private final KafkaTemplate<String, GroundDataSourceDTO> sourceRegistrationTemplate;
    private final KafkaConfigurationProperties kafkaConfigurationProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if(applicationContext == null || !applicationReadyEvent.getApplicationContext().equals(applicationContext)) {
            return;
        }
        GroundDataSourceDTO groundDataSourceConfiguration = getSourceConfiguration();
        ListenableFuture<SendResult<String, GroundDataSourceDTO>> future = sourceRegistrationTemplate.send(
                kafkaConfigurationProperties.getTopicName(KafkaTopics.SOURCE_TOPIC),
                groundDataSourceConfiguration.getName(),
                groundDataSourceConfiguration
        );
        future.addCallback(this::onSuccess, this::onFailure);
    }

    private GroundDataSourceDTO getSourceConfiguration() {
        return GroundDataSourceDTO.builder()
                .name(sourceConfigurationProperties.getName())
                .fullName(sourceConfigurationProperties.getFullName())
                .dataUrl(sourceConfigurationProperties.getDataUrl())
                .heartBeatUrl(sourceConfigurationProperties.getHeartBeatUrl())
                .reindexUrl(sourceConfigurationProperties.getReindexUrl())
                .reindexStatusUrl(sourceConfigurationProperties.getReindexStatusUrl())
                .parameters(Collections.emptyList())
                .minDate(LocalDate.of(1990, Month.JANUARY, 1))
                .maxDate(LocalDate.now())
                .build();
    }

    private void onSuccess(SendResult<String, GroundDataSourceDTO> sendResult) {
        log.info("Successfully sent source registration event");
    }

    private void onFailure(Throwable throwable) {
        log.error("Source not registered! Cause: ", throwable);
    }
}
