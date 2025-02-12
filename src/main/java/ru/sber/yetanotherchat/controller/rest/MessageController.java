package ru.sber.yetanotherchat.controller.rest;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sber.yetanotherchat.dto.FetchHistoryDto;
import ru.sber.yetanotherchat.dto.HistoryDto;
import ru.sber.yetanotherchat.service.MessagingService;
import ru.sber.yetanotherchat.validation.NotZero;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {
    private final MessagingService service;

    @GetMapping(
            path = "/messages",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HistoryDto> history(
            @RequestParam @NotZero Long peerId,
            @RequestParam(required = false) Long offsetId,
            @RequestParam @PositiveOrZero Integer limit,
            Principal principal) {
        log.info("Запрос на получение истории сообщений c peer = {} от пользователя {}", peerId, principal.getName());

        var messageDtos = service.fetchHistory(
                FetchHistoryDto.builder()
                        .peerId(peerId)
                        .offsetId(offsetId)
                        .limit(limit)
                        .build(),
                principal);

        return ResponseEntity
                .ok(HistoryDto.builder()
                        .messages(messageDtos)
                        .build()
                );
    }
}