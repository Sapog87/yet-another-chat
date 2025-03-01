package ru.sber.yetanotherchat.controller.websocket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.sber.yetanotherchat.dto.MessageDto;
import ru.sber.yetanotherchat.dto.SendMessageDto;
import ru.sber.yetanotherchat.service.MessagingService;

import java.security.Principal;

/**
 * Контроллер для обработки WebSocket сообщений.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {
    private final MessagingService service;

    /**
     * Обрабатывает сообщение от пользователя.
     *
     * @param sendMessageDto Сообщение
     * @param principal      Пользователь, отправляющий сообщение
     * @return {@link MessageDto}
     */
    @MessageMapping("/chat/message")
    @SendToUser("/topic/update")
    public MessageDto message(@Payload @Valid SendMessageDto sendMessageDto, Principal principal) {
        log.info("Start WebSocketMessageController::message with sendMessageDto: {}",
                sendMessageDto);

        return service.sendMessage(sendMessageDto, principal);
    }

}
