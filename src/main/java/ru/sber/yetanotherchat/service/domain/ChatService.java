package ru.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.entity.UserChat;
import ru.sber.yetanotherchat.exception.ChatNotFoundException;
import ru.sber.yetanotherchat.repository.ChatRepository;
import ru.sber.yetanotherchat.repository.UserChatRepository;

import java.util.List;
import java.util.Set;

import static ru.sber.yetanotherchat.exception.ErrorMessages.CHAT_WITH_SUCH_ID_NOT_EXISTS;
import static ru.sber.yetanotherchat.exception.ErrorMessages.PERSONAL_CHAT_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;

    /**
     * @param user
     * @param recipient
     * @return
     */
    @Transactional
    public Chat findOrCreatePersonalChat(User user, User recipient) {
        return chatRepository.findPersonalChatByUsers(user, recipient)
                .orElseGet(() -> {
                    var chat = chatRepository.save(getPersonalChat(user, recipient));
                    userChatRepository.save(getUserChat(user, chat));
                    userChatRepository.save(getUserChat(recipient, chat));
                    return chat;
                });
    }

    /**
     * @param user
     * @param recipient
     * @return
     */
    public Chat findPersonalChat(User user, User recipient) {
        return chatRepository.findPersonalChatByUsers(user, recipient)
                .orElseThrow(() -> new ChatNotFoundException(PERSONAL_CHAT_NOT_EXIST));
    }

    /**
     * @param id
     * @return
     */
    public Chat findChatById(Long id) {
        return chatRepository.findById(id)
                .orElseThrow(() -> new ChatNotFoundException(CHAT_WITH_SUCH_ID_NOT_EXISTS));
    }

    private Chat getPersonalChat(User firstUser, User secondUser) {
        var chat = new Chat();
        chat.setIsGroup(false);
        chat.setMembers(Set.of(firstUser, secondUser));
        return chat;
    }

    private UserChat getUserChat(User user, Chat chat) {
        var userChat = new UserChat();
        userChat.setUser(user);
        userChat.setChat(chat);
        return userChat;
    }

    /**
     * @param user
     * @param chat
     * @return
     */
    public boolean isMemberOfChat(User user, Chat chat) {
        return userChatRepository.existsByChatAndUser(chat, user);
    }

    /**
     * @param user
     * @param name
     * @return
     */
    @Transactional
    public Chat createGroupChat(User user, String name) {
        var chat = new Chat();
        chat.setIsGroup(true);
        chat.setMembers(Set.of(user));
        chat.setGroupChatName(name);
        return chatRepository.save(chat);
    }

    public List<Chat> findAllGroupsByName(String name) {
        return chatRepository.findChatByGroupChatNameContainingIgnoreCaseAndIsGroup(name, true);
    }
}