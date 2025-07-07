package com.example.do_next.repository;

import com.example.do_next.entity.Todo;
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
    
    //在 TodoRepository 中定义的大部分方法，Spring Data JPA 会根据方法名自动推断出要执行的查询
    //根据用户ID查找所有待办事项
    List<Todo> findByUserId(Long userId);
    
    //根据用户ID和完成状态查找待办事项
    List<Todo> findByUserIdAndCompleted(Long userId, Boolean completed);
    
    //根据父待办项ID查找子待办事项
    List<Todo> findByParentId(Long parentId);
    
    //查找用户的主待办事项（无父项的待办事项）
    List<Todo> findByUserIdAndParentIdIsNull(Long userId);
    
    //根据用户ID和优先级查找待办事项
    List<Todo> findByUserIdAndPriority(Long userId, Integer priority);
    
    //根据用户ID和重要性查找待办事项
    List<Todo> findByUserIdAndImportance(Long userId, Integer importance);
    
    //方法名无法清晰地表达所需的查询逻辑，或者需要执行更复杂、定制化的查询时，需要使用 @Query 注解来手动编写
    //@Qurey支持原生sql与jpql
    //jpql操作的是实体对象及其属性，而不是数据库表和列。这意味着在 JPQL 中使用实体类名和属性名，而不是真实的表名和列名
    //查找过期的待办事项
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.dueDate < :now AND t.completed = false")
    List<Todo> findOverdueTodos(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    //根据标题模糊查询用户的待办事项
    List<Todo> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title);
    
    //统计用户的待办事项数量
    long countByUserIdAndCompleted(Long userId, Boolean completed);
    
    //@Param 注解用于指定查询参数的名称，以便在 @Query 中引用(前加冒号)
    //根据用户ID和日期范围查找待办事项
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.dueDate BETWEEN :startDate AND :endDate")
    List<Todo> findByUserIdAndDueDateBetween(@Param("userId") Long userId, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
}
