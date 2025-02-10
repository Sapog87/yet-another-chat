package org.sber.yetanotherchat.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.dto.chat.ChatInfo;
import org.sber.yetanotherchat.service.ChatInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatInfoService chatInfoService;

    @GetMapping
    public List<ChatInfo> findChatsByName(@RequestParam @NotBlank String name) {
        return chatInfoService.findChatsByName(name);
    }
}