package ru.sber.yetanotherchat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfigurer implements WebSocketMessageBrokerConfigurer {

    public static final String WEBSOCKET_STOMP_PATH = "/ws";
    public static final String APP_DESTINATION_STOMP_PREFIX = "/app";
    public static final String USER_DESTINATION_STOMP_PREFIX = "/user";
    public static final String BROKER_DESTINATION_STOMP_PREFIX = "/chat";
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