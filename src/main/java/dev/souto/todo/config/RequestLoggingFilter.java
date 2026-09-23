package dev.souto.todo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(
        RequestLoggingFilter.class
    );
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        long start = System.nanoTime();
        String previousRequestId = MDC.get(REQUEST_ID_MDC_KEY);

        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            logger.atInfo()
                .addKeyValue("httpMethod", request.getMethod())
                .addKeyValue("requestUri", request.getRequestURI())
                .addKeyValue("status", response.getStatus())
                .addKeyValue("durationMs", durationMs)
                .log("HTTP request completed");
            if (previousRequestId == null) {
                MDC.remove(REQUEST_ID_MDC_KEY);
            } else {
                MDC.put(REQUEST_ID_MDC_KEY, previousRequestId);
            }
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (
            requestId != null &&
            requestId.length() <= 100 &&
            requestId.matches("[A-Za-z0-9._-]+")
        ) {
            return requestId;
        }
        return UUID.randomUUID().toString();
    }
}
