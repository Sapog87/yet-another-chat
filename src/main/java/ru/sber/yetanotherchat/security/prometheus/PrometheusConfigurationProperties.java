package ru.sber.yetanotherchat.security.prometheus;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Учетные данные, которые использует Prometheus для доступа к метрикам.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "prometheus.credentials")
public class PrometheusConfigurationProperties {
    private String username;
    private String password;
}
