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

    public static final String[] NO_AUTH_URLS = {"/css/*", "/js/*", "/login", "/signup", "/error"};
    public static final String[] ANY_AUTH_URLS = {"/", "/ws", "/api/**"};
    public static final String[] ACTUATOR_URLS = {"/actuator/**"};
    public static final String LOGIN_URL = "/login";

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
                                .requestMatchers(NO_AUTH_URLS).permitAll()
                                .requestMatchers(ANY_AUTH_URLS).hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(ACTUATOR_URLS).hasAuthority("ADMIN")
                                .anyRequest().denyAll()
                )
                .logout(Customizer.withDefaults())
                .formLogin(formLogin ->
                        formLogin
                                .loginPage(LOGIN_URL)
                                .defaultSuccessUrl("/")
                                .successHandler(new SimpleUrlAuthenticationSuccessHandler("/"))
                                .permitAll()
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
