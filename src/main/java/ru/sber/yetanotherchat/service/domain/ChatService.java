package ru.sber.yetanotherchat.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.yetanotherchat.entity.Chat;
import ru.sber.yetanotherchat.entity.User;
import ru.sber.yetanotherchat.entity.UserChat;
import ru.sber.yetanotherchat.exception.ChatNotFoundException;
import ru.sber.yetanotherchat.repository.ChatRepository;
import ru.sber.yetanotherchat.repository.UserChatRepository;

import java.util.List;

/**
 *
 */
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
                    if (!user.equals(recipient)) {
                        userChatRepository.save(getUserChat(recipient, chat));
                    }
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
                .orElseThrow(() -> new ChatNotFoundException(
                        "Личного чата между пользователями {%d} и {%d} не существует"
                                .formatted(user.getId(), recipient.getId()))
                );
    }

    /**
     * @param id
     * @return
     */
    public Chat findChatById(Long id) {
        return chatRepository.findById(id)
                .orElseThrow(() -> new ChatNotFoundException(
                        "Чата с таким id {%d} не существует"
                                .formatted(id))
                );
    }

    private Chat getPersonalChat(User firstUser, User secondUser) {
        var chat = new Chat();
        chat.setIsGroup(false);
        chat.setMembers(List.of(firstUser, secondUser));
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
        var chat = chatRepository.save(getGroupChat(user, name));
        userChatRepository.save(getUserChat(user, chat));
        return chat;
    }

    private Chat getGroupChat(User firstUser, String name) {
        var chat = new Chat();
        chat.setIsGroup(true);
        chat.setMembers(List.of(firstUser));
        chat.setGroupChatName(name);
        return chat;
    }

    /**
     * @param name
     * @return
     */
    public List<Chat> findAllGroupsByName(String name, Integer page, Integer size) {
        if (page == null || page < 0) page = 0;
        if (size == null || size < 0) size = 20;
        return chatRepository.findChatByGroupChatNameContainingIgnoreCaseAndIsGroup(name, true, PageRequest.of(page, size));
    }

    /**
     * @param user
     * @return
     */
    public List<Chat> findAllChatsByUser(User user) {
        var chats = userChatRepository.findAllByUser(user);
        return chats.stream().map(UserChat::getChat).toList();
    }
}