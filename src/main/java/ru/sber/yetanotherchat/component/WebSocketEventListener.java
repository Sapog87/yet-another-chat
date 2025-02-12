package ru.sber.yetanotherchat.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    @EventListener
    public void handle(SessionDisconnectEvent event) {
        log.info("Received session disconnect event: {}", event);
    }

    @EventListener
    public void handle(SessionConnectedEvent event) {
        log.info("Received session connected event: {}", event);
    }

    @EventListener
    public void handle(SessionSubscribeEvent event) {
        log.info("Received session subscribe event: {}", event);
    }

    @EventListener
    public void handle(SessionUnsubscribeEvent event) {
        log.info("Received session unsubscribe event: {}", event);
    }
}