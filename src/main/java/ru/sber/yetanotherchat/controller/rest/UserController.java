package ru.sber.yetanotherchat.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sber.yetanotherchat.dto.PeerDto;
import ru.sber.yetanotherchat.dto.PeerSearchDto;
import ru.sber.yetanotherchat.dto.ServerError;
import ru.sber.yetanotherchat.service.AccountService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final AccountService accountService;

    @Operation(summary = "Поиск пользователей по имени")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ в случае успеха",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PeerSearchDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServerError.class))),
    })
    @GetMapping(
            path = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PeerSearchDto> getUsers(@RequestParam @NotBlank String name) {
        var users = accountService.getUsersByName(name);

        var peerDtos = users.stream().map(user -> PeerDto.builder()
                .peerId(user.getId())
                .peerName(user.getName())
                .type(PeerDto.PeerType.USER)
                .build()
        ).toList();

        return ResponseEntity
                .ok(PeerSearchDto.builder()
                        .peers(peerDtos)
                        .build()
                );
    }
}