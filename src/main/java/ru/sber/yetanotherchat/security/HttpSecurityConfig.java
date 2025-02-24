package ru.sber.yetanotherchat.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Конфигурация фильтров Spring Security для HTTP-запросов.
 */
@Configuration
public class HttpSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/css/*", "/js/*").permitAll()
                                .requestMatchers("/login", "/signup", "/error").permitAll()
                                .requestMatchers("/actuator/**").hasAnyAuthority("ADMIN")
                                .anyRequest().authenticated()
                )
                .logout(Customizer.withDefaults())
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .defaultSuccessUrl("/")
                                .successHandler(new SimpleUrlAuthenticationSuccessHandler("/"))
                                .permitAll()
                );


        return http.build();
    }
}
