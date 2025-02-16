package ru.sber.yetanotherchat.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.service.MessagingService;
import ru.sber.yetanotherchat.validation.NotZero;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {
    private final MessagingService service;

    @Operation(summary = "Получение истории сообщений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = HistoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
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