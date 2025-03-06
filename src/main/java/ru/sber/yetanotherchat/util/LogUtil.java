package ru.sber.yetanotherchat.util;

import lombok.experimental.UtilityClass;
import org.slf4j.MDC;

import java.util.UUID;

@UtilityClass
public class LogUtil {
    public static final String REQUEST_ID = "requestId";

    public static String addRequestId() {
        var requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId);
        return requestId;
    }

    public static void removeRequestId() {
        MDC.remove(REQUEST_ID);
    }
}
