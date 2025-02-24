package ru.sber.yetanotherchat.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sber.yetanotherchat.dto.GroupResponse;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.service.GroupService;

import java.security.Principal;
import java.util.List;

/**
 * Контроллер, отвечающий за управление группами
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    /**
     * Создание новой группы.
     *
     * @param name      Имя группы (обязательное поле).
     * @param principal Текущий пользователь.
     */
    @Operation(summary = "Создание группы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ответ в случае успеха", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @PostMapping(
            path = "/groups",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GroupResponse> createGroup(@RequestParam(value = "name") @NotBlank String name,
                                                     Principal principal) {
        log.info("Запрос на создание группы с именем {} от пользователя {}", name, principal.getName());

        var groupDto = groupService.createGroup(name, principal);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GroupResponse.builder()
                        .peerId(groupDto.getId())
                        .name(groupDto.getName())
                        .isMember(groupDto.getIsMember())
                        .build()
                );
    }

    /**
     * Поиск групп по имени или его части с поддержкой пагинации.
     *
     * @param name      Имя группы (обязательное поле).
     * @param page      Номер страницы (по умолчанию 0).
     * @param pageSize  Размер страницы (по умолчанию 20).
     * @param principal Текущий пользователь.
     */
    @Operation(summary = "Поиск групп")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = GroupResponse.class)))),
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @GetMapping(
            path = "/groups",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<GroupResponse>> getGroups(@RequestParam(name = "name") @NotBlank String name,
                                                         @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                         @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                         Principal principal) {
        log.info("Запрос на поиск групп с именем = {} от пользователя {}", name, principal.getName());

        var groups = groupService.getGroupsByName(name, page, pageSize, principal);

        if (groups.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var groupDtos = groups.stream().map(group -> GroupResponse.builder()
                .peerId(group.getId())
                .name(group.getName())
                .isMember(group.getIsMember())
                .build()
        ).toList();

        return ResponseEntity.ok(groupDtos);
    }

    /**
     * Вступление в группу.
     *
     * @param peerId    Идентификатор группы (должен быть отрицательным).
     * @param principal Текущий пользователь.
     */
    @Operation(summary = "Вступление в группу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @PostMapping(
            path = "/groups/{peerId}/members"
    )
    public ResponseEntity<GroupResponse> participateInGroup(@PathVariable("peerId") @Negative Long peerId,
                                                            Principal principal) {
        log.info("Запрос на вступление в группу с peerId = {} от пользователя {}", peerId, principal.getName());

        var groupDto = groupService.participateInGroup(peerId, principal);

        return ResponseEntity
                .ok(GroupResponse.builder()
                        .peerId(groupDto.getId())
                        .name(groupDto.getName())
                        .isMember(groupDto.getIsMember())
                        .build()
                );
    }

    /**
     * Выход из группы.
     *
     * @param peerId    Идентификатор группы (должен быть отрицательным).
     * @param principal Текущий пользователь.
     */
    @Operation(summary = "Выход из группы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @DeleteMapping(
            path = "/groups/{peerId}/members"
    )
    public ResponseEntity<GroupResponse> leaveGroup(@PathVariable("peerId") @Negative Long peerId,
                                                    Principal principal) {
        log.info("Запрос на выход из группы с peerId = {} от пользователя {}", peerId, principal.getName());

        var groupDto = groupService.leaveGroup(peerId, principal);

        return ResponseEntity
                .ok(GroupResponse.builder()
                        .peerId(groupDto.getId())
                        .name(groupDto.getName())
                        .isMember(groupDto.getIsMember())
                        .build()
                );
    }
}