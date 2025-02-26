package ru.sber.yetanotherchat.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

/**
 * Конфигурация для работы с Prometheus.
 */
@Configuration
@EnableConfigurationProperties(PrometheusConfigurationProperties.class)
public class PrometheusSecurityConfig {

    @Bean(name = "prometheus")
    public UserDetailsService prometheusUserDetailsService(
            PrometheusConfigurationProperties properties,
            PasswordEncoder passwordEncoder) {
        var user = new User(
                properties.getUsername(),
                passwordEncoder.encode(properties.getPassword()),
                List.of(new SimpleGrantedAuthority("PROMETHEUS"))
        );
        return new InMemoryUserDetailsManager(user);
    }
}
