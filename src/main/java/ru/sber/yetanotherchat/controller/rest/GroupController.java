package ru.sber.yetanotherchat.controller.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sber.yetanotherchat.dto.PeerDto;
import ru.sber.yetanotherchat.service.GroupService;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping(
            path = "/group",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PeerDto> createGroup(@RequestParam @NotBlank String name, Principal principal) {
        log.info("Запрос на создание группы с именем {}", name);

        var chatDto = groupService.createGroup(name, principal);

        var peerInfo = PeerDto.builder()
                .peerId(-chatDto.getId())
                .peerName(chatDto.getName())
                .type(PeerDto.PeerType.GROUP)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(peerInfo);
    }
}