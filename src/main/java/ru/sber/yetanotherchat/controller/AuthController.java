package ru.sber.yetanotherchat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sber.yetanotherchat.dto.UserRegistrationDto;
import ru.sber.yetanotherchat.exception.UserAlreadyExistsException;
import ru.sber.yetanotherchat.service.AccountService;


/**
 * Контроллер для регистрации и авторизации пользователей.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AccountService userService;

    /**
     * Возвращает страницу регистрации.
     *
     * @param model Модель для передачи данных в представление.
     */
    @GetMapping("/signup")
    public String signup(Model model) {
        var userRegistrationDto = new UserRegistrationDto();
        model.addAttribute("user", userRegistrationDto);
        return "signup";
    }

    /**
     * Регистрация пользователя.
     *
     * @param createDto Данные из формы регистрации.
     */
    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") @Valid UserRegistrationDto createDto) {
        try {
            userService.registerUser(createDto);
        } catch (UserAlreadyExistsException e) {
            return "redirect:/signup?error";
        }

        return "forward:/login";
    }

    /**
     * Возвращает страницу аутентификации.
     */
    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }
}