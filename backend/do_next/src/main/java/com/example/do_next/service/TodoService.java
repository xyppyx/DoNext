package com.example.do_next.service;

import com.example.do_next.entity.Todo;
import com.example.do_next.entity.User;
import com.example.do_next.repository.TodoRepository;
import com.example.do_next.exception.NotFoundException;
import com.example.do_next.exception.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.do_next.dto.TodoDto;
import com.example.do_next.dto.UserDto;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

/**
 * TodoService - 待办事项业务逻辑服务类
 * 
 * 核心职责：
 * 1. 管理待办事项的CRUD操作
 * 2. 处理主任务与子任务的层级关系（类似进程与线程的概念）
 * 3. 确保数据所有权安全（用户只能操作自己的待办事项）
 * 4. 执行业务规则验证
 */
@Service // Spring注解：标识这是一个服务层组件，会被Spring容器管理，自动注册为Bean
public class TodoService {
    
    /**
     * @Autowired注解：自动依赖注入
     * Spring会自动查找TodoRepository类型的Bean并注入到这个字段中
     * 用于与数据库进行交互，执行CRUD操作
     */
    @Autowired
    private TodoRepository todoRepository;
    
    /**
     * 创建待办项
     * 
     * 业务逻辑：
     * 1. 将新的待办项关联到当前登录用户
     * 2. 如果指定了父待办项，验证父项的存在性和所有权
     * 3. 保存到数据库
     * 
     * @param todo 要创建的待办项对象
     * @param currentUser 当前登录的用户
     * @return 保存后的待办项对象（包含自动生成的ID）
     */
    public Todo createTodo(Todo todo, User currentUser) {
        // 业务规则：将待办项关联到当前用户，确保数据所有权
        todo.setUser(currentUser);
        
        // 业务规则：如果指定了父待办项，需要验证其存在性和所有权
        if (todo.getParentTodo() != null) {
            // 调用getTodoById进行验证：
            // 1. 验证父待办项是否存在
            // 2. 验证父待办项是否属于当前用户
            // 如果验证失败，getTodoById会抛出异常
            getTodoById(todo.getParentTodo().getTodoId(), currentUser);
        }
        
        // JPA操作：保存到数据库，@PrePersist会自动设置创建时间
        return todoRepository.save(todo);
    }
    
    /**
     * 获取用户所有待办项
     * 
     * 业务逻辑：根据用户对象查询该用户的所有待办事项
     * 
     * @param user 用户对象
     * @return 该用户的所有待办项列表
     */
    public List<Todo> getAllTodosByUser(User user) {
        // JPA查询：利用Repository的方法名查询规则
        // findByUser会生成SQL: SELECT * FROM todos WHERE user_id = ?
        return todoRepository.findByUser(user);
    }
    
    /**
     * 获取用户的主待办项（无父项的待办项）
     * 
     * 业务逻辑：类似于获取所有"主进程"，即没有父项的顶级任务
     * 
     * @param user 用户对象
     * @return 该用户的主待办项列表
     */
    public List<Todo> getMainTodosByUser(User user) {
        // JPA查询：查找指定用户且parent_todo为NULL的记录
        // 对应SQL: SELECT * FROM todos WHERE user_id = ? AND parent_id IS NULL
        return todoRepository.findByUserAndParentTodoIsNull(user);
    }
    
    /**
     * 获取指定主待办项的子待办项
     * 
     * 业务逻辑：类似于获取某个"进程"下的所有"线程"
     * 
     * @param parentTodo 父待办项对象
     * @param currentUser 当前用户（用于权限验证）
     * @return 该父待办项下的所有子待办项
     */
    public List<Todo> getSubTodosByParent(Todo parentTodo, User currentUser) {
        // 安全检查：先验证父待办项是否属于当前用户
        getTodoById(parentTodo.getTodoId(), currentUser);
        
        // JPA查询：查找所有以指定Todo为父项的子待办项
        return todoRepository.findByParentTodo(parentTodo);
    }
    
    /**
     * 根据ID获取单个待办项（包含所有权检查）
     */
    public Todo getTodoById(Long id, User currentUser) {
        // JPA操作：根据主键查找，返回Optional防止空指针
        Optional<Todo> todo = todoRepository.findById(id);
        
        // 业务验证：检查待办项是否存在
        if (todo.isEmpty()) {
            throw NotFoundException.todoNotFound(id);
        }
        
        // 安全检查：验证数据所有权，防止用户访问他人的数据
        if (!todo.get().getUser().getUserId().equals(currentUser.getUserId())) {
            throw AccessDeniedException.ownershipViolation("待办项");
        }
        
        return todo.get();
    }
    
    /**
     * 更新待办项
     * 
     * 业务逻辑：
     * 1. 先验证用户对该待办项的所有权
     * 2. 更新允许修改的字段
     * 3. 保存到数据库
     * 
     * @param id 待办项ID
     * @param updatedTodo 包含更新数据的待办项对象
     * @param currentUser 当前用户
     * @return 更新后的待办项对象
     */
    public Todo updateTodo(Long id, Todo updatedTodo, User currentUser) {
        // 安全检查：获取待更新的Todo并验证所有权
        Todo existingTodo = getTodoById(id, currentUser);
        
        // 业务逻辑：更新允许修改的字段
        // 注意：不更新user、创建时间等敏感字段
        existingTodo.setTitle(updatedTodo.getTitle());
        existingTodo.setDescription(updatedTodo.getDescription());
        existingTodo.setCompleted(updatedTodo.getCompleted());
        existingTodo.setProgress(updatedTodo.getProgress());
        existingTodo.setDueDate(updatedTodo.getDueDate());
        existingTodo.setPriority(updatedTodo.getPriority());
        existingTodo.setImportance(updatedTodo.getImportance());
        
        // JPA操作：保存更新，@PreUpdate会自动更新修改时间
        return todoRepository.save(existingTodo);
    }
    
    /**
     * 删除待办项
     * 
     * 业务逻辑：
     * 1. 验证用户对该待办项的所有权
     * 2. 执行删除操作
     * 3. 数据库的CASCADE设置会自动删除所有子待办项
     * 
     * @param id 待办项ID
     * @param currentUser 当前用户
     */
    public void deleteTodo(Long id, User currentUser) {
        // 安全检查：获取待删除的Todo并验证所有权
        Todo todo = getTodoById(id, currentUser);
        
        // JPA操作：执行删除
        // 由于数据库设置了ON DELETE CASCADE，删除主待办项会自动删除其所有子待办项
        // 这符合"删除进程会终止其所有线程"的设计理念
        todoRepository.delete(todo);
    }

    /**
     * 获取用户所有待办项（返回DTO）
     */
    public List<TodoDto> getAllTodosDtoByUser(User user) {
        List<Todo> todos = todoRepository.findByUser(user);
        return todos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户主待办项DTO
     */
    public List<TodoDto> getMainTodosDtoByUser(User user) {
        List<Todo> mainTodos = todoRepository.findByUserAndParentTodoIsNull(user);
        return mainTodos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取子待办项DTO
     */
    public List<TodoDto> getSubTodosDtoByParent(Todo parentTodo, User currentUser) {
        // 验证权限
        getTodoById(parentTodo.getTodoId(), currentUser);
        
        List<Todo> subTodos = todoRepository.findByParentTodo(parentTodo);
        return subTodos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取待办项DTO
     */
    public TodoDto getTodoDtoById(Long id, User currentUser) {
        Todo todo = getTodoById(id, currentUser);
        return convertToDto(todo);
    }
    
    /**
     * 转换Todo实体为DTO
     */
    public TodoDto convertToDto(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.setTodoId(todo.getTodoId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setCompleted(todo.getCompleted());
        dto.setProgress(todo.getProgress());
        dto.setPriority(todo.getPriority());
        dto.setImportance(todo.getImportance());
        dto.setDueDate(todo.getDueDate());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setUpdatedAt(todo.getUpdatedAt());
        
        // 转换用户信息（只包含安全信息）
        if (todo.getUser() != null) {
            UserDto userDto = new UserDto();
            userDto.setUserId(todo.getUser().getUserId());
            userDto.setUserName(todo.getUser().getUserName());
            userDto.setEmail(todo.getUser().getEmail());
            userDto.setUserRole(todo.getUser().getUserRole());
            userDto.setCreatedAt(todo.getUser().getCreatedAt());
            userDto.setLastLoginAt(todo.getUser().getLastLoginAt());
            dto.setUser(userDto);
        }
        
        // 转换父待办项信息（简化版，避免循环引用）
        if (todo.getParentTodo() != null) {
            TodoDto.ParentTodoDto parentDto = new TodoDto.ParentTodoDto();
            parentDto.setTodoId(todo.getParentTodo().getTodoId());
            parentDto.setTitle(todo.getParentTodo().getTitle());
            parentDto.setCompleted(todo.getParentTodo().getCompleted());
            parentDto.setProgress(todo.getParentTodo().getProgress());
            dto.setParentTodo(parentDto);
        }
        
        return dto;
    }

    
}
