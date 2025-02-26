package ru.sber.yetanotherchat.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки конфигурации для пользователя Prometheus.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "prometheus.credentials")
public class PrometheusConfigurationProperties {
    private String username;
    private String password;
}
