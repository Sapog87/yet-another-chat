package ru.sber.yetanotherchat.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Конфигурация фильтров Spring Security для HTTP-запросов.
 */
@Configuration
@ComponentScan
public class BaseHttpSecurityConfig {

    private static final String[] NO_AUTH_URLS = {"/css/*", "/js/*", "/login", "/signup", "/error"};
    private static final String[] ANY_AUTH_URLS = {"/", "/ws", "/api/**"};
    private static final String[] ACTUATOR_URLS = {"/actuator/**"};

    @Bean
    @Order
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager
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
                                .loginPage("/login")
                                .defaultSuccessUrl("/")
                                .successHandler(new SimpleUrlAuthenticationSuccessHandler("/"))
                                .permitAll()
                )
                .authenticationManager(authenticationManager);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationProvider authenticationProvider) {
        var providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
