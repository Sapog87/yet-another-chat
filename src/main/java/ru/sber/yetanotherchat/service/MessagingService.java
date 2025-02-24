package ru.sber.yetanotherchat.service;

import ru.sber.yetanotherchat.dto.FetchHistoryDto;
import ru.sber.yetanotherchat.dto.MessageDto;
import ru.sber.yetanotherchat.dto.SendMessageDto;

import java.security.Principal;
import java.util.List;

/**
 * Интерфейс для работы с сообщениями.
 */
public interface MessagingService {
    /**
     * Отправка сообщения.
     * <p>
     * Обрабатывает отправку сообщения пользователем.
     * Сообщение будет отправлено в соответствующий чат.
     *
     * @param sendMessageDto объект, содержащий данные для отправки сообщения
     * @param sender         пользователь, отправляющий сообщение
     * @return {@link MessageDto}
     */
    MessageDto sendMessage(SendMessageDto sendMessageDto, Principal sender);

    /**
     * Получение истории сообщений.
     * <p>
     * Этот метод используется для получения истории сообщений между двумя пользователями.
     *
     * @param fetchHistoryDto объект, содержащий данные для получения сообщения
     * @param sender          пользователь, запрашивающий историю
     * @return {@link List<MessageDto>}
     */
    List<MessageDto> fetchHistory(FetchHistoryDto fetchHistoryDto, Principal sender);
}
