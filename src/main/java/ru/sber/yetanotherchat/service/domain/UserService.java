package ru.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.exception.UserNotFoundException;
import ru.sber.yetanotherchat.repository.UserRepository;

import java.util.List;
import java.util.Set;

/**
 * Сервис для работы с {@link User}
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Создает нового пользователя с указанными данными.
     *
     * @param username имя пользователя
     * @param password пароль пользователя
     * @param name     имя пользователя
     * @return {@link User} - созданный пользователь
     */
    public User createUser(String username, CharSequence password, String name) {
        var user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRoles(Set.of(User.Role.USER));
        return userRepository.save(user);
    }

    /**
     * Проверяет, существует ли пользователь с указанным именем пользователя.
     *
     * @param username имя пользователя
     * @return true, если пользователь существует, иначе false
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя
     * @return User - найденный пользователь
     * @throws UserNotFoundException если пользователь не найден
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return {@link User} - найденный пользователь
     * @throws UserNotFoundException если пользователь не найден
     */
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    /**
     * Находит пользователей по части имени с поддержкой пагинации.
     *
     * @param name часть имени для поиска
     * @param page номер страницы для пагинации
     * @param size размер страницы
     * @return {@link List<User>} - список пользователей, удовлетворяющих поисковому запросу
     */
    public List<User> findAllUsersByName(String name, Integer page, Integer size) {
        if (page == null || page < 0) page = 0;
        if (size == null || size < 0) size = 20;
        return userRepository.findAllByNameContainingIgnoreCase(name, PageRequest.of(page, size));
    }

    /**
     * Находит всех участников чата.
     *
     * @param chat чат, члены которого нужно найти
     * @return {@link List<User>} - список участников чата
     */
    public List<User> findChatMembers(Chat chat) {
        return userRepository.findAllByChats(chat);
    }

    /**
     * Находит другого участника личного чата (кроме указанного пользователя).
     *
     * @param user текущий пользователь
     * @param chat чат, в котором нужно найти другого участника
     * @return {@link User} - другой участник чата
     * @throws IllegalArgumentException если чат является групповым
     * @throws UserNotFoundException    если другой участник чата не найден
     */
    public User findOtherMemberOfPersonalChat(User user, Chat chat) {
        if (Boolean.TRUE.equals(chat.getIsGroup())) {
            throw new IllegalArgumentException(
                    "Чат {%d} является групповым"
                            .formatted(chat.getId()));
        }

        var users = chat.getMembers();

        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException(
                    "У чата {%d} нет пользователей"
                            .formatted(chat.getId()));
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