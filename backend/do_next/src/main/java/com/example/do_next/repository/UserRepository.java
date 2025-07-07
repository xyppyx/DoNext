package com.example.do_next.repository;

import com.example.do_next.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    //根据用户名查找用户
    Optional<User> findByUsername(String username);
    
    //根据邮箱查找用户
    Optional<User> findByEmail(String email);
    
    //根据用户名或邮箱查找用户（用于登录）
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    //检查用户名是否存在
    boolean existsByUsername(String username);
    
    //检查邮箱是否存在
    boolean existsByEmail(String email);
    
    //根据角色查找用户
    List<User> findByUserRole(String userRole);
    
    //查找特定时间段内创建的用户
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    //查找最近登录的用户
    @Query("SELECT u FROM User u WHERE u.lastLoginAt IS NOT NULL ORDER BY u.lastLoginAt DESC")
    List<User> findRecentlyLoggedInUsers();
    
    //更新用户最后登录时间
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLoginAt(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);
    
    //根据用户名模糊查询
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    //统计特定角色的用户数量
    long countByUserRole(String userRole);
    
    //查找长时间未登录的用户
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :cutoffDate OR u.lastLoginAt IS NULL")
    List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);
}
