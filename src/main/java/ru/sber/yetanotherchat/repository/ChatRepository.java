package ru.sber.yetanotherchat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(value = "SELECT c " +
                   "FROM Chat c " +
                   "JOIN UserChat uc1 ON c = uc1.chat " +
                   "JOIN UserChat uc2 ON c = uc2.chat " +
                   "WHERE c.isGroup = false " +
                   "AND uc1.user = :user1 " +
                   "AND uc2.user = :user2")
    Optional<Chat> findPersonalChatByUsers(@Param("user1") User user1, @Param("user2") User user2);

    List<Chat> findChatByGroupChatNameContainingIgnoreCaseAndIsGroup(String groupChatName, Boolean isGroup);
}