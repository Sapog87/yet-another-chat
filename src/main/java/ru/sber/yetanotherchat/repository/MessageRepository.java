package ru.sber.yetanotherchat.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findMessagesByChatOrderByIdDesc(Chat chat, Limit limit);

    List<Message> findMessagesByChatAndIdLessThanOrderByIdDesc(Chat chat,
                                                               Long offsetId,
                                                               Limit limit);
}
