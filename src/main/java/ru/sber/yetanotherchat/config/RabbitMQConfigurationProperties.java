package ru.sber.yetanotherchat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Учетные данные, для подключения к RabbitMQ.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "rabbit")
public class RabbitMQConfigurationProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}
