package org.ppietrzak.grounddatacore.health;

import org.ppietrzak.grounddatacore.api.healthcheck.HealthCheckDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthResource {

    @GetMapping
    public HealthCheckDTO getHealthCheck() {
        return HealthCheckDTO.builder()
                .isHealthy(true)
                .build();
    }
}
