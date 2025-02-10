package org.sber.yetanotherchat.controller;

import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.dto.chat.ChatInfo;
import org.sber.yetanotherchat.service.ChatInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ChatInfoService chatInfoService;

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        List<ChatInfo> chats = chatInfoService.findChatsByUser(principal.getName());

        model.addAttribute("chats", chats);

        return "index";
    }
}