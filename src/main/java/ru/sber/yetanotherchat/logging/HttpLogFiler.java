package ru.sber.yetanotherchat.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import org.springframework.stereotype.Component;
import ru.sber.yetanotherchat.util.LogUtil;

import java.io.IOException;

/**
 * Фильтр, добавляющий сквозной requestId в логи http запросов.
 */
@Component
public class HttpLogFiler extends HttpFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            LogUtil.addRequestId();
            chain.doFilter(request, response);
        } finally {
            LogUtil.removeRequestId();
        }
    }
}
