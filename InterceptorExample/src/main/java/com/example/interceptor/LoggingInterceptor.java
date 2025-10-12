package com.example.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    // Runs BEFORE controller method
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("[preHandle] Request URL: {}", request.getRequestURL());
        logger.info("Method: {}, IP: {}", request.getMethod(), request.getRemoteAddr());

        // Simple auth check example
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.toLowerCase().contains("bearer")) {
            logger.warn("Unauthorized request - missing or invalid token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token");
            return false; // Stops request from reaching controller
        }

        return true; // Proceed to controller
    }

    // Runs AFTER controller method, before view rendering
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        logger.info("[postHandle] Request processed successfully for {}", request.getRequestURI());
    }

    // Runs AFTER complete request cycle (even after exception)
    //its like finally
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("[afterCompletion] Completed request for {}", request.getRequestURI());
        if (ex != null) {
            logger.error("Exception during request processing: {}", ex.getMessage());
        }
    }
}
