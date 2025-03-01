package ru.sber.yetanotherchat.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Positive;
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
import ru.sber.yetanotherchat.dto.MessageDto;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.service.MessagingService;
import ru.sber.yetanotherchat.validation.NotZero;

import java.security.Principal;
import java.util.List;

/**
 * Контроллер, отвечающий за управление сообщениями.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {
    private final MessagingService service;

    /**
     * Получение истории сообщений с пользователем или группой.
     *
     * @param peerId    Идентификатор чата (обязательный, не может быть равен нулю).
     * @param offsetId  Смещение для пагинации (опционально, положительное число).
     * @param limit     Ограничение количества возвращаемых сообщений (по умолчанию 0).
     * @param principal Текущий пользователь.
     * @return {@link ResponseEntity<List<MessageDto>>}
     */
    @Operation(summary = "Получение истории сообщений")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ответ в случае успеха",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = MessageDto.class)))),
            @ApiResponse(
                    responseCode = "204",
                    description = "No Content",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = Void.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ServerError.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ServerError.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ServerError.class))),
    })
    @GetMapping(
            path = "/messages",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<MessageDto>> history(
            @RequestParam(name = "peerId") @NotZero Long peerId,
            @RequestParam(name = "offsetId", required = false) @Positive Long offsetId,
            @RequestParam(name = "limit", required = false, defaultValue = "0") @PositiveOrZero Integer limit,
            Principal principal) {
        log.info("Start MessageController::history with peerId: {}, offsetId: {}, limit: {}",
                peerId, offsetId, limit);

        var messageDtos = service.fetchHistory(
                FetchHistoryDto.builder()
                        .peerId(peerId)
                        .offsetId(offsetId)
                        .limit(limit)
                        .build(),
                principal);

        if (messageDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(messageDtos);
    }
}
