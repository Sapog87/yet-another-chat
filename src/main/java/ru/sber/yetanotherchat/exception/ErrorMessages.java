package ru.sber.yetanotherchat.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {
    public static final String USER_ALREADY_EXISTS = "Пользователь с таким username уже существует";
    public static final String USER_WITH_SUCH_ID_NOT_EXISTS = "Пользователь с таким id не существует";
    public static final String USER_WITH_SUCH_USERNAME_NOT_EXISTS = "Пользователь с таким username не существует";

    public static final String PERSONAL_CHAT_NOT_EXIST = "Личного чата между пользователями не существует";
    public static final String CHAT_WITH_SUCH_ID_NOT_EXISTS = "Чата с таким id не существует";

    public static final String INVALID_PEER = "Неверный peer";
    public static final String PEER_ACCESS_DENIED = "У вас нет доступа к данному peer";

    public static final String PEER_NOT_MEMBER = "Вы не являетесь членом группы";
}