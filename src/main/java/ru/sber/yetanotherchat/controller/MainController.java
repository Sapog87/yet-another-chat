package ru.sber.yetanotherchat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping
    public String index(Model model, Principal principal) {

        return "index";
    }
}