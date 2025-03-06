package ru.sber.yetanotherchat.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import ru.sber.yetanotherchat.util.LogUtil;

import java.io.IOException;

/**
 * Фильтр, добавляющий сквозной requestId в логи http запросов.
 */
@Component
public class HttpLogFiler extends HttpFilter {

    public static final String X_REQUEST_ID = "X-Request-Id";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            var requestId = LogUtil.addRequestId();
            response.setHeader(X_REQUEST_ID, requestId);
            chain.doFilter(request, response);
        } finally {
            LogUtil.removeRequestId();
        }
    }
}
