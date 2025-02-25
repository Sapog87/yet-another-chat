package ru.sber.yetanotherchat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.sber.yetanotherchat.service.PeerService;

import java.security.Principal;

/**
 * Контроллер для главной страницы.
 */
@Controller
@RequiredArgsConstructor
public class MainController {
    private final PeerService peerService;

    /**
     * Возвращает главную старицу.
     *
     * @param model     Модель для передачи данных в представление.
     * @param principal Текущий пользователь.
     * @return view
     */
    @GetMapping
    public String index(Model model, Principal principal) {
        var peers = peerService.getAllChats(principal);
        model.addAttribute("peers", peers);

        return "index";
    }
}
