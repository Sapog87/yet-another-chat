package ru.sber.yetanotherchat.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.sber.yetanotherchat.dto.GroupResponseList;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.service.GroupService;

import java.security.Principal;
import java.util.Collections;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

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
    public ResponseEntity<GroupResponse> createGroup(@RequestParam("name") @NotBlank String name,
                                                     Principal principal) {
        log.info("Запрос на создание группы с именем {} от пользователя {}", name, principal.getName());

        var groupDto = groupService.createGroup(name, principal);

        var response = GroupResponse.builder()
                .peerId(-groupDto.getId())
                .name(groupDto.getName())
                .isMember(groupDto.getIsMember())
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Поиск групп")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GroupResponseList.class))),
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GroupResponseList.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @GetMapping(
            path = "/groups",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GroupResponseList> getGroups(@RequestParam(name = "name") @NotBlank String name,
                                                       @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                       @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                       Principal principal) {
        log.info("Запрос на поиск групп с именем = {} от пользователя {}", name, principal.getName());
        var groups = groupService.getGroupsByName(name, page, pageSize, principal);

        if (groups.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(GroupResponseList.builder()
                            .groups(Collections.emptyList())
                            .build()
                    );
        }

        var groupDtos = groups.stream().map(group -> GroupResponse.builder()
                .peerId(-group.getId())
                .name(group.getName())
                .isMember(group.getIsMember())
                .build()
        ).toList();

        return ResponseEntity
                .ok(GroupResponseList.builder()
                        .groups(groupDtos)
                        .build()
                );
    }

    @Operation(summary = "Вступление в группу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @PostMapping(
            path = "/groups/{peerId}/members"
    )
    public ResponseEntity<Void> participateInGroup(@PathVariable("peerId") @Negative Long peerId, Principal principal) {
        log.info("Запрос на вступление в группу с peerId = {} от пользователя {}", peerId, principal.getName());
        var id = Math.abs(peerId);
        groupService.participateInGroup(id, principal);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Выход из группы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @DeleteMapping(
            path = "/groups/{peerId}/members"
    )
    public ResponseEntity<Void> leaveGroup(@PathVariable("peerId") @Negative Long peerId, Principal principal) {
        log.info("Запрос на выход из группы с peerId = {} от пользователя {}", peerId, principal.getName());
        var id = Math.abs(peerId);
        groupService.leaveGroup(id, principal);

        return ResponseEntity.ok().build();
    }
}