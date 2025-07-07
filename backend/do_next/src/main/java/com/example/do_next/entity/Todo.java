package com.example.do_next.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
@Data
public class Todo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "completed", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean completed = false;

    @Column(name = "progress", columnDefinition = "INTEGER DEFAULT 0")
    private Integer progress = 0;

    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "priority", columnDefinition = "INTEGER DEFAULT 0")
    private Integer priority = 0;

    @Column(name = "importance", columnDefinition = "INTEGER DEFAULT 0")
    private Integer importance = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public Todo() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Todo(Long userId, String title) {
        this();
        this.userId = userId;
        this.title = title;
    }
    
    // PrePersist和PreUpdate回调
    @PrePersist//作用: 在实体被持久化之前执行, 用于初始化创建和更新时间
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate//作用: 在实体被更新之前执行, 用于更新更新时间
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
