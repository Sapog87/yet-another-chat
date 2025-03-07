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
 * Сервис для работы с {@link Chat}.
 */
@Service
@RequiredArgsConstructor
public class ChatService {
    public static final int DEFAULT_LIMIT_SIZE = 20;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;

    /**
     * Находит или создает личный чат между двумя пользователями.
     * Если чат уже существует, возвращается его экземпляр, иначе создается новый.
     *
     * @param user      пользователь, который запрашивает чат
     * @param recipient пользователь, с которым нужно создать чат
     * @return {@link Chat} - найденный или созданный чат
     */
    @Transactional
    public Chat findOrCreatePersonalChat(User user, User recipient) {
        return chatRepository.findPersonalChatByUserAndPeerId(user, recipient.getId())
                .orElseGet(() -> {
                    var chat = chatRepository.save(getPersonalChat(user, recipient));
                    userChatRepository.save(getUserChat(user, chat, recipient.getId()));
                    if (!user.equals(recipient)) {
                        userChatRepository.save(getUserChat(recipient, chat, user.getId()));
                    }
                    return chat;
                });
    }

    /**
     * Находит личный чат между двумя пользователями.
     * Если чат не найден, выбрасывает исключение ChatNotFoundException.
     *
     * @param user      первый пользователь
     * @param recipient второй пользователь
     * @return {@link Chat} - найденный чат
     * @throws ChatNotFoundException если чат не найден
     */
    public Chat findPersonalChat(User user, User recipient) {
        return chatRepository.findPersonalChatByUserAndPeerId(user, recipient.getId())
                .orElseThrow(() -> new ChatNotFoundException(
                        "Личного чата между пользователями {%d} и {%d} не существует"
                                .formatted(user.getId(), recipient.getId()))
                );
    }

    /**
     * Находит чат по его id.
     * Если чат не найден, выбрасывает исключение ChatNotFoundException.
     *
     * @param id id чата
     * @return {@link Chat} - найденный чат
     * @throws ChatNotFoundException если чат с данным id не найден
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

    private UserChat getUserChat(User user, Chat chat, Long peerId) {
        var userChat = new UserChat();
        userChat.setUser(user);
        userChat.setChat(chat);
        userChat.setPeerId(peerId);
        return userChat;
    }

    /**
     * Проверяет, является ли пользователь участником чата.
     *
     * @param user пользователь
     * @param chat чат
     * @return true, если пользователь является участником чата; false в противном случае
     */
    public boolean isMemberOfChat(User user, Chat chat) {
        return userChatRepository.existsByChatAndUser(chat, user);
    }

    /**
     * Создает групповой чат.
     *
     * @param user пользователь, создающий групповой чат
     * @param name имя группы
     * @return {@link Chat} - созданный групповой чат
     */
    @Transactional
    public Chat createGroupChat(User user, String name) {
        var group = chatRepository.save(getGroupChat(user, name));
        userChatRepository.save(getUserChat(user, group, -group.getId()));
        return group;
    }

    private Chat getGroupChat(User firstUser, String name) {
        var chat = new Chat();
        chat.setIsGroup(true);
        chat.setMembers(List.of(firstUser));
        chat.setGroupChatName(name);
        return chat;
    }

    /**
     * Находит все групповые чаты, имя которых содержит переданное значение (с учетом регистра).
     * Поддерживает пагинацию.
     *
     * @param name часть имени группового чата для поиска
     * @param page номер страницы для пагинации
     * @param size количество элементов на странице
     * @return {@link List<Chat>} - список чатов, удовлетворяющих запросу
     */
    public List<Chat> findAllGroupsByName(String name, Integer page, Integer size) {
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 0) {
            size = DEFAULT_LIMIT_SIZE;
        }
        return chatRepository.findChatByGroupChatNameContainingIgnoreCaseAndIsGroup(
                name, true, PageRequest.of(page, size));
    }

    /**
     * Находит все чаты пользователя.
     *
     * @param user пользователь
     * @return {@link List<Chat>} - список чатов, в которых состоит пользователь
     */
    public List<Chat> findAllChatsByUser(User user) {
        var chats = userChatRepository.findAllByUser(user);
        return chats.stream().map(UserChat::getChat).toList();
    }

    /**
     * Добавляет пользователя в группу.
     *
     * @param user  пользователь
     * @param group группа
     */
    public void participateInGroup(User user, Chat group) {
        userChatRepository.save(getUserChat(user, group, -group.getId()));
    }
}
