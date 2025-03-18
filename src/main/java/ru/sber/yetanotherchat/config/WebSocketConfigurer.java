package ru.sber.yetanotherchat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * Конфигурация WebSocket для приложения с использованием STOMP-протокола.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitMQConfigurationProperties.class)
public class WebSocketConfigurer implements WebSocketMessageBrokerConfigurer {

    private final List<ChannelInterceptor> interceptors;

    private final RabbitMQConfigurationProperties properties;

    /**
     * Путь для подключения к WebSocket.
     */
    public static final String WEBSOCKET_STOMP_PATH = "/ws";
    /**
     * Префикс для маршрутов приложений.
     */
    public static final String APP_DESTINATION_STOMP_PREFIX = "/app";
    /**
     * Префикс для маршрутов с привязкой к пользователю.
     */
    public static final String USER_DESTINATION_STOMP_PREFIX = "/user";
    /**
     * Префикс для маршрутов брокера сообщений.
     */
    public static final String BROKER_DESTINATION_STOMP_PREFIX = "/chat";
    /**
     * Префикс для общих тем.
     */
    public static final String TOPIC = "/topic";
    /**
     * Маршрут для обработки пользователей не подключенных к серверу.
     */
    public static final String UNRESOLVED_USER_DESTINATION = "/topic/unresolved-user-destination";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay(BROKER_DESTINATION_STOMP_PREFIX, TOPIC)
                .setRelayHost(properties.getHost())
                .setRelayPort(properties.getPort())
                .setClientLogin(properties.getUsername())
                .setClientPasscode(properties.getPassword())
                .setSystemLogin(properties.getUsername())
                .setSystemPasscode(properties.getPassword())
                .setUserDestinationBroadcast(UNRESOLVED_USER_DESTINATION);

        config.setApplicationDestinationPrefixes(APP_DESTINATION_STOMP_PREFIX);
        config.setUserDestinationPrefix(USER_DESTINATION_STOMP_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WEBSOCKET_STOMP_PATH);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(interceptors.toArray(new ChannelInterceptor[0]));
    }
}
