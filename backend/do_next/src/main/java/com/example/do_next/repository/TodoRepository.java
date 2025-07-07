package com.example.do_next.repository;

import com.example.do_next.entity.Todo;
import com.example.do_next.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository//注解作用: 数据访问层
//JpaRepository 是 Spring Data JPA 提供的一个接口，它预定义了许多常用的 CRUD 操作
//TodoRepository 接口继承 JpaRepository，提供对 Todo 实体的 CRUD 操作
//<Todo, Long>表示该接口操作的实体类是 Todo，主键类型是 Long
public interface TodoRepository extends JpaRepository<Todo, Long> {
    
    // 根据用户对象查找所有待办事项
    List<Todo> findByUser(User user);
    
    // 根据用户对象和完成状态查找待办事项
    List<Todo> findByUserAndCompleted(User user, Boolean completed);
    
    // 根据父待办项对象查找子待办事项
    List<Todo> findByParentTodo(Todo parentTodo);
    
    // 查找用户的主待办事项（无父项的待办事项）
    List<Todo> findByUserAndParentTodoIsNull(User user);
    
    // 根据用户对象和优先级查找待办事项
    List<Todo> findByUserAndPriority(User user, Integer priority);
    
    // 根据用户对象和重要性查找待办事项
    List<Todo> findByUserAndImportance(User user, Integer importance);
    
    // 查找过期的待办事项
    @Query("SELECT t FROM Todo t WHERE t.user = :user AND t.dueDate < :now AND t.completed = false")
    List<Todo> findOverdueTodos(@Param("user") User user, @Param("now") LocalDateTime now);
    
    // 根据标题模糊查询用户的待办事项
    List<Todo> findByUserAndTitleContainingIgnoreCase(User user, String title);
    
    // 统计用户的待办事项数量
    long countByUserAndCompleted(User user, Boolean completed);
    
    // 根据用户对象和日期范围查找待办事项
    @Query("SELECT t FROM Todo t WHERE t.user = :user AND t.dueDate BETWEEN :startDate AND :endDate")
    List<Todo> findByUserAndDueDateBetween(@Param("user") User user, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
}
