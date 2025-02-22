package ru.sber.yetanotherchat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Конфигурация WebSocket для приложения с использованием STOMP-протокола.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfigurer implements WebSocketMessageBrokerConfigurer {

    /**
     * Путь для подключения к WebSocket
     */
    public static final String WEBSOCKET_STOMP_PATH = "/ws";
    /**
     * Префикс для маршрутов приложений
     */
    public static final String APP_DESTINATION_STOMP_PREFIX = "/app";
    /**
     * Префикс для маршрутов с привязкой к пользователю
     */
    public static final String USER_DESTINATION_STOMP_PREFIX = "/user";
    /**
     * Префикс для маршрутов брокера сообщений
     */
    public static final String BROKER_DESTINATION_STOMP_PREFIX = "/chat";
    /**
     * Префикс для общих тем
     */
    public static final String TOPIC = "/topic";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(BROKER_DESTINATION_STOMP_PREFIX, TOPIC);
        config.setApplicationDestinationPrefixes(APP_DESTINATION_STOMP_PREFIX);
        config.setUserDestinationPrefix(USER_DESTINATION_STOMP_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WEBSOCKET_STOMP_PATH);
    }
}