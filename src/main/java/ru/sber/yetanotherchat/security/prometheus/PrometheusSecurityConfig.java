package ru.sber.yetanotherchat.security.prometheus;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * Конфигурация для работы с Prometheus.
 */
@Configuration
@EnableConfigurationProperties(PrometheusConfigurationProperties.class)
@ConditionalOnProperty(prefix = "prometheus.credentials", name = {"username", "password"})
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

    @Bean
    @Order(1)
    public SecurityFilterChain prometheusSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("prometheus") UserDetailsService userDetailsService
    ) throws Exception {
        http
                .securityMatcher("/actuator/prometheus")
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().hasAnyAuthority("PROMETHEUS", "ADMIN")
                )
                .httpBasic(Customizer.withDefaults())
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
