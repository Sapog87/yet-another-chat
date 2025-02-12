package ru.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.UserNotFoundException;
import ru.sber.yetanotherchat.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    public List<User> findAllUsersByName(String name) {
        return userRepository.findAllByNameContainingIgnoreCase(name);
    }

    public List<User> findChatMembers(Chat chat) {
        return userRepository.findAllByChats(chat);
    }
}