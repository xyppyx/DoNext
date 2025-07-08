package com.example.do_next.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TodoDto - 待办项数据传输对象
 * 
 * 作用：
 * 1. 控制返回给前端的字段
 * 2. 避免JPA关联对象的循环引用
 * 3. 减少不必要的数据传输
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {
    private Long todoId;
    private String title;
    private String description;
    private Boolean completed;
    private Integer progress;
    private Integer priority;
    private Integer importance;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 用户信息（简化版）
    private UserDto user;
    
    // 父待办项信息（简化版，避免循环引用）
    private ParentTodoDto parentTodo;
    
    /**
     * 简化的父待办项DTO，只包含基本信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParentTodoDto {
        private Long todoId;
        private String title;
        private Boolean completed;
        private Integer progress;
    }
}
