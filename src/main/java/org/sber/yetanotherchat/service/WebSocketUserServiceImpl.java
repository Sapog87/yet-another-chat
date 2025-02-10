package org.sber.yetanotherchat.service;

import lombok.RequiredArgsConstructor;
import org.sber.yetanotherchat.dto.message.StatusOutputMessage;
import org.sber.yetanotherchat.entity.User;
import org.sber.yetanotherchat.exception.UserNotFoundException;
import org.sber.yetanotherchat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketUserServiceImpl implements WebSocketUserService {
    private final UserRepository userRepository;
//    private final UserStatusService userStatusService;

    @Override
    @Transactional
    public List<StatusOutputMessage> getStatuses(Principal sender) {
        User user = userRepository.findByUsername(sender.getName())
                .orElseThrow(UserNotFoundException::new);

        return List.of();
    }
}