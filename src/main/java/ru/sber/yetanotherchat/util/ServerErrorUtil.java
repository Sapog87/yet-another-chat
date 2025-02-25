package ru.sber.yetanotherchat.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.ObjectError;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerErrorUtil {
    public static Map<String, String> getStringStringHashMap(HandlerMethodValidationException e) {
        var errors = new HashMap<String, String>();
        e.getValueResults().forEach(
                result -> result.getResolvableErrors()
                        .forEach(error -> {
                            String param = (error instanceof ObjectError objectError
                                    ? objectError.getObjectName()
                                    : ((MessageSourceResolvable) Objects.requireNonNull(error.getArguments())[0])
                                    .getDefaultMessage());

                            param = (result.getContainerIndex() != null
                                    ? param + "[" + result.getContainerIndex() + "]"
                                    : param);

                            errors.put(param, error.getDefaultMessage());
                        })
        );
        return errors;
    }
}
