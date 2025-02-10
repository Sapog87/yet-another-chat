package org.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.entity.User;
import org.sber.yetanotherchat.exception.UserNotFoundException;
import org.sber.yetanotherchat.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }
}
