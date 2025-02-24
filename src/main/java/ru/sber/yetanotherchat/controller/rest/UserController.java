package ru.sber.yetanotherchat.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.dto.UserResponse;
import ru.sber.yetanotherchat.service.AccountService;
import ru.sber.yetanotherchat.service.StatusService;

import java.security.Principal;
import java.util.List;

import static ru.sber.yetanotherchat.dto.Status.OFFLINE;
import static ru.sber.yetanotherchat.dto.Status.ONLINE;

/**
 * Контроллер для управления пользователями.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final AccountService accountService;
    private final StatusService statusService;


    /**
     * Поиск пользователей по имени или его части с поддержкой пагинации.
     *
     * @param name      Имя пользователя (не может быть пустым).
     * @param page      Номер страницы (по умолчанию 0).
     * @param pageSize  Размер страницы (по умолчанию 20).
     * @param principal Текущий пользователь.
     */
    @Operation(summary = "Поиск пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @GetMapping(
            path = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(name = "name") @NotBlank String name,
                                                       @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                       @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                       Principal principal) {
        log.info("Запрос на поиск пользователей с именем = {} от пользователя {}", name, principal.getName());
        var users = accountService.getUsersByName(name, page, pageSize);

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var userDtos = users.stream()
                .map(user -> UserResponse.builder()
                        .peerId(user.getId())
                        .name(user.getName())
                        .status(statusService.isOnline(user.getId()) ? ONLINE : OFFLINE)
                        .build()
                ).toList();

        return ResponseEntity.ok(userDtos);
    }

    /**
     * Поиск пользователя по id.
     *
     * @param peerId    Идентификатор пользователя (должен быть положительным).
     * @param principal Информация о текущем пользователе.
     */
    @Operation(summary = "Поиск пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @GetMapping(
            path = "/users/{peerId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponse> getUser(@PathVariable @Positive Long peerId,
                                                Principal principal) {
        log.info("Запрос на поиск пользователей с id = {} от пользователя {}", peerId, principal.getName());
        var userDto = accountService.getUserById(peerId);

        return ResponseEntity
                .ok(UserResponse.builder()
                        .peerId(userDto.getId())
                        .name(userDto.getName())
                        .status(statusService.isOnline(userDto.getId()) ? ONLINE : OFFLINE)
                        .build()
                );
    }
}