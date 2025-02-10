package org.sber.yetanotherchat.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {
    public static final String USER_WITH_ID_NOT_FOUND = "Пользователь с id: {%s} не найден";
    public static final String USER_WITH_LOGIN_ALREADY_EXISTS = "Пользователь с login: {%s} уже существует";
}