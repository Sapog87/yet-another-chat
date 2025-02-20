package ru.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.UserNotFoundException;
import ru.sber.yetanotherchat.repository.UserRepository;

import java.util.List;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * @param username
     * @return
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    /**
     * @param id
     * @return
     */
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    /**
     * @param name
     * @param page
     * @param size
     * @return
     */
    public List<User> findAllUsersByName(String name, Integer page, Integer size) {
        if (page == null || page < 0) page = 0;
        if (size == null || size < 0) size = 20;
        return userRepository.findAllByNameContainingIgnoreCase(name, PageRequest.of(page, size));
    }

    /**
     * @param chat
     * @return
     */
    public List<User> findChatMembers(Chat chat) {
        return userRepository.findAllByChats(chat);
    }

    /**
     * @param user
     * @param chat
     * @return
     */
    public User findOtherMemberOfPersonalChat(User user, Chat chat) {
        if (Boolean.TRUE.equals(chat.getIsGroup())) {
            throw new IllegalArgumentException("Chat is group");
        }

        var users = chat.getMembers();

        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException(
                    "Other user not found (chat %d, user %d)"
                            .formatted(chat.getId(), user.getId()));
        }

        if (users.size() == 1) {
            return users.get(0);
        }

        if (users.get(0).equals(user)) {
            return users.get(1);
        }
        return users.get(0);
    }
}