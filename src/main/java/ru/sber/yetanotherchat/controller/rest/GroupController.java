package ru.sber.yetanotherchat.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sber.yetanotherchat.dto.PeerDto;
import ru.sber.yetanotherchat.dto.PeerSearchDto;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.service.GroupService;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @Operation(summary = "Создание группы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PeerDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @PostMapping(
            path = "/groups",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PeerDto> createGroup(@RequestParam @NotBlank String name, Principal principal) {
        log.info("Запрос на создание группы с именем {} от пользователя {}", name, principal.getName());

        var chatDto = groupService.createGroup(name, principal);

        var peerDto = PeerDto.builder()
                .peerId(-chatDto.getId())
                .peerName(chatDto.getName())
                .type(PeerDto.PeerType.GROUP)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(peerDto);
    }

    @Operation(summary = "Поиск групп по имени")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PeerSearchDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @GetMapping(
            path = "/groups",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PeerSearchDto> getUsers(@RequestParam @NotBlank String name) {
        var groups = groupService.getGroupsByName(name);

        var peerDtos = groups.stream().map(group -> PeerDto.builder()
                .peerId(-group.getId())
                .peerName(group.getName())
                .type(PeerDto.PeerType.GROUP)
                .build()
        ).toList();

        return ResponseEntity
                .ok(PeerSearchDto.builder()
                        .peers(peerDtos)
                        .build()
                );
    }
}