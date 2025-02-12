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
import ru.sber.yetanotherchat.service.UserRegistrationService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserRegistrationService userService;

    @GetMapping("/signup")
    public String signup(Model model) {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        model.addAttribute("user", userRegistrationDto);
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") @Valid UserRegistrationDto createDto) {
        try {
            userService.registerUser(createDto);
        } catch (UserAlreadyExistsException e) {
            return "redirect:/signup?error";
        }

        return "redirect:/login";
    }

    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }
}