package ru.sber.yetanotherchat.repository;

import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.entity.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {
    boolean existsByChatAndUser(Chat chat, User user);
}