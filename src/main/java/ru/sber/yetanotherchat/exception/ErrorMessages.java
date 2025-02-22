package ru.sber.yetanotherchat.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {
    public static final String INTERNAL_ERROR = "Внутренняя ошибка сервера";

    public static final String INVALID_PEER = "Недействительный пир";
    public static final String UNREACHABLE_PEER = "Отсутствует доступ к пир";
    public static final String PEER_NOT_FOUND = "Пир не существует";
}