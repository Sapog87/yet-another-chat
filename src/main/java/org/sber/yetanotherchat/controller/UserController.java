package org.sber.yetanotherchat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.dto.user.UserCreateDto;
import org.sber.yetanotherchat.dto.user.UserDto;
import org.sber.yetanotherchat.service.UserInfoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserInfoService userInfoService;

    @GetMapping(path = "/users/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUser(@PathVariable long id) {
        UserDto user = userInfoService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping(path = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateDto userDto) {
        UserDto createdUser = userInfoService.createUser(userDto);
        URI location = URI.create("/api/users/" + createdUser.getId());
        return ResponseEntity.created(location).body(createdUser);
    }
}