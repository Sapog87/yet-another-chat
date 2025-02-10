package org.sber.yetanotherchat.repository;

import org.sber.yetanotherchat.entity.Chat;
import org.sber.yetanotherchat.entity.Message;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findMessagesByChat(Chat chat, Limit limit);

    @Query(value = "select m from Message m where m.chat = :chat and m.id < :offsetId")
    List<Message> findMessagesByChatWithOffset(@Param("chat") Chat chat, @Param("offsetId") Long offsetId, Limit limit);

    boolean existsByRandomIdAndSenderId(Long randomId, Long senderId);
}