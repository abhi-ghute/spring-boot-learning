package com.example.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class PerformanceInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (long) request.getAttribute("startTime");
        long timeTaken = System.currentTimeMillis() - startTime;

        if (timeTaken > 1000) { // alert for slow requests (>1s)
            logger.warn("🐢 Slow request [{}] took {} ms", request.getRequestURI(), timeTaken);
        } else {
            logger.info("⚡ Fast request [{}] took {} ms", request.getRequestURI(), timeTaken);
        }
    }
}