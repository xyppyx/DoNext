package com.example.do_next.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity               //声明这是一个实体类，将与数据库表进行映射
@Table(name = "users") //指定数据库表名
@Data                 //使用Lombok的@Data注解自动生成getter、setter、toString等方法
@NoArgsConstructor    //使用Lombok的@NoArgsConstructor注解生成无参构造函数
@AllArgsConstructor   //使用Lombok的@AllArgsConstructor注解生成全参构造函数
public class User {

    @Id //指定主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) //主键自增策略
    private Long userId;

    @Column(nullable = false, unique = true, length = 255) //指定列属性，不能为空且唯一
    private String userName;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, length = 255) 
    private String email;

    @Column(name = "user_role", nullable = false, length = 50)
    private String userRole = "USER"; //默认角色为USER

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}