package com.example.do_next.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserDto - 用户数据传输对象
 * 
 * 作用：
 * 1. 过滤敏感信息（如密码）
 * 2. 控制序列化的字段
 * 3. 减少网络传输数据量
 * 4. 提供标准化的用户信息响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String userName;
    private String email;
    private String userRole;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    // 注意：不包含password、updatedAt等敏感或不必要的信息
}
