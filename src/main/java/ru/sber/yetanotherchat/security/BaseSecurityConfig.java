package ru.sber.yetanotherchat.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sber.yetanotherchat.repository.UserRepository;

/**
 * Конфигурация бинов необходимых для работы security layer.
 */
@Configuration
public class BaseSecurityConfig {

    public static final String CACHE_KEY = "users";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        UserCache userCache = new SpringCacheBasedUserCache(new ConcurrentMapCache(CACHE_KEY));
        UserDetailsService delegate = new DaoUserDetailsService(userRepository);
        CachingUserDetailsService userDetailsService = new CachingUserDetailsService(delegate);
        userDetailsService.setUserCache(userCache);
        return userDetailsService;
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder builder) {
        builder.eraseCredentials(false);
    }
}
