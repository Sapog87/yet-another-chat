package ru.sber.yetanotherchat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(value = "select uc.chat from UserChat uc where uc.user = :user and uc.peerId = :peerId")
    Optional<Chat> findPersonalChatByUserAndPeerId(@Param("user") User user,
                                                   @Param("peerId") Long peerId);

    List<Chat> findChatByGroupChatNameContainingIgnoreCaseAndIsGroup(String groupChatName,
                                                                     Boolean isGroup,
                                                                     Pageable pageable);
}
