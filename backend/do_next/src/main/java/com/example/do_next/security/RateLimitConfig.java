package com.example.do_next.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RateLimitConfig - API限流配置
 * 
 * 作用：
 * 1. 防止API被恶意频繁调用
 * 2. 保护服务器资源
 * 3. 提高系统稳定性
 */
@Configuration
public class RateLimitConfig {
    
    @Bean
    public OncePerRequestFilter rateLimitFilter() {
        return new RateLimitFilter();
    }
    
    private static class RateLimitFilter extends OncePerRequestFilter {
        
        private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
        private final int maxRequestsPerMinute = 60; // 每分钟最多60次请求
        
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                FilterChain filterChain) throws ServletException, IOException {
            
            String clientIp = getClientIP(request);
            String key = clientIp + ":" + (System.currentTimeMillis() / 60000); // 按分钟分组
            
            AtomicInteger requestCount = requestCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
            
            if (requestCount.incrementAndGet() > maxRequestsPerMinute) {
                response.setStatus(429); // Too Many Requests
                response.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
                return;
            }
            
            // 清理过期的计数器
            cleanupExpiredCounters();
            
            filterChain.doFilter(request, response);
        }
        
        private String getClientIP(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        
        private void cleanupExpiredCounters() {
            long currentMinute = System.currentTimeMillis() / 60000;
            requestCounts.entrySet().removeIf(entry -> {
                String[] parts = entry.getKey().split(":");
                long minute = Long.parseLong(parts[1]);
                return currentMinute - minute > 5; // 保留最近5分钟的数据
            });
        }
    }
}
