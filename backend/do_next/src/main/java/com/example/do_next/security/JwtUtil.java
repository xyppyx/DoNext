package com.example.do_next.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JwtUtil - JWT工具类
 * 
 * 作用：
 * 1. 生成JWT Token
 * 2. 验证JWT Token
 * 3. 从Token中提取用户信息
 * 4. 管理Token过期时间
 */
@Component
public class JwtUtil {
    
    @Value("${app.jwt.secret:mySecretKeyForDoNextApplication2024}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration:86400000}") // 24小时，单位毫秒
    private Long jwtExpiration;
    
    private SecretKey getSigningKey() {
        // 确保密钥长度足够（至少32字节用于HS256）
        if (jwtSecret.getBytes().length < 32) {
            jwtSecret = jwtSecret + "0".repeat(32 - jwtSecret.getBytes().length);
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * 生成JWT Token
     */
    public String generateToken(String username, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .subject(username)  // 修复：使用subject()而不是setSubject()
                .claim("userId", userId)
                .issuedAt(now)      // 修复：使用issuedAt()而不是setIssuedAt()
                .expiration(expiryDate)  // 修复：使用expiration()而不是setExpirationTime()
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    
    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }
    
    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 检查Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }
    
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()  // 修复：使用parser()而不是parserBuilder()
                .verifyWith(getSigningKey())  // 修复：使用verifyWith()而不是setSigningKey()
                .build()
                .parseSignedClaims(token)  // 修复：使用parseSignedClaims()而不是parseClaimsJws()
                .getPayload();  // 修复：使用getPayload()而不是getBody()
    }
}
