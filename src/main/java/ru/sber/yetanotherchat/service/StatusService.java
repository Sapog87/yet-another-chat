package ru.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.sber.yetanotherchat.dto.Online;
import ru.sber.yetanotherchat.service.domain.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatusService {
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Long, Set<String>> onlineUsers = new ConcurrentHashMap<>();

    /**
     * @param userId
     * @return
     */
    public boolean isOnline(Long userId) {
        return !Optional
                .ofNullable(onlineUsers.get(userId))
                .orElse(Collections.emptySet())
                .isEmpty();
    }


    @EventListener
    protected void handle(SessionDisconnectEvent event) {
        log.info("Received session disconnect event: {}", event);

        var user = userService.findUserByUsername(Objects.requireNonNull(event.getUser()).getName());

        var headers = StompHeaderAccessor.wrap(event.getMessage());
        var sessions = Optional.ofNullable(onlineUsers.get(user.getId())).orElse(Collections.emptySet());

        if (sessions.isEmpty()) {
            return;
        }

        if (sessions.size() == 1) {
            sessions.remove(headers.getSessionId());
            var ids = getIdsOfOnlineUsers();
            broadcast(ids);
        } else {
            sessions.remove(headers.getSessionId());
        }
    }

    @EventListener
    protected void handle(SessionConnectedEvent event) {
        log.info("Received session connected event: {}", event);

        var user = userService.findUserByUsername(Objects.requireNonNull(event.getUser()).getName());

        var headers = StompHeaderAccessor.wrap(event.getMessage());
        onlineUsers.computeIfAbsent(user.getId(), k -> new HashSet<>()).add(headers.getSessionId());

        var ids = getIdsOfOnlineUsers();
        broadcast(ids);
    }

    private Set<Long> getIdsOfOnlineUsers() {
        return onlineUsers.entrySet().stream()
                .filter(kv -> !kv.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private void broadcast(Set<Long> ids) {
        messagingTemplate.convertAndSend(
                "/topic/status",
                Online.builder()
                        .ids(ids)
                        .build()
        );
    }

}
