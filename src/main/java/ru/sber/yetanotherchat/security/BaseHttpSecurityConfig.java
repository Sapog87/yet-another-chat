package ru.sber.yetanotherchat.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Конфигурация фильтров Spring Security для HTTP-запросов.
 */
@Configuration
@ComponentScan
public class BaseHttpSecurityConfig {
    @Bean
    @Order
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserDetailsService userDetailsService
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/css/*", "/js/*").permitAll()
                                .requestMatchers("/login", "/signup", "/error").permitAll()
                                .requestMatchers("/", "/ws", "/api/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers("/actuator/**").hasAuthority("ADMIN")
                                .anyRequest().denyAll()
                )
                .logout(Customizer.withDefaults())
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .defaultSuccessUrl("/")
                                .successHandler(new SimpleUrlAuthenticationSuccessHandler("/"))
                                .permitAll()
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
