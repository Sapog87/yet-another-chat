package ru.sber.yetanotherchat.repository;

import ru.sber.yetanotherchat.entity.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {
    List<UserMessage> findAllByReceiverUsernameAndReadAtIsNull(String username);
}
