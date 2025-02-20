package ru.sber.yetanotherchat.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorStatuses {
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", 500),
    BAD_REQUEST("BAD_REQUEST", 400),
    NOT_FOUND("NOT_FOUND", 404);

    private final String text;
    private final Integer code;
}
