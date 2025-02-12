package ru.sber.yetanotherchat.controller.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sber.yetanotherchat.dto.PeerDto;
import ru.sber.yetanotherchat.service.GroupService;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/group")
    public ResponseEntity<PeerDto> createGroup(@RequestParam @NotBlank String name, Principal principal) {
        var chatDto = groupService.createGroup(name, principal);
        var peerInfo = PeerDto.builder()
                .peerId(-chatDto.getId())
                .peerName(chatDto.getName())
                .type(PeerDto.PeerType.GROUP)
                .build();
        return ResponseEntity.ok(peerInfo);
    }
}