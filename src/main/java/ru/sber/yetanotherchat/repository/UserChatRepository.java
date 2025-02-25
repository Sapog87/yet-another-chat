package ru.sber.yetanotherchat.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.entity.UserChat;

import java.util.List;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {
    boolean existsByChatAndUser(Chat chat, User user);

    List<UserChat> findAllByUser(@NotNull User user);
}
