package com.example.do_next.controller;

import com.example.do_next.entity.Todo;
import com.example.do_next.entity.User;
import com.example.do_next.dto.TodoDto;
import com.example.do_next.dto.UserDto;
import com.example.do_next.service.TodoService;
import com.example.do_next.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.do_next.exception.ValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TodoController - 待办事项控制器
 * 
 * 职责：
 * 1. 处理HTTP请求和响应
 * 2. 参数验证和格式转换
 * 3. 调用Service层业务逻辑
 * 4. 异常处理和错误响应
 */
@RestController // 组合注解：@Controller + @ResponseBody，返回JSON格式数据
@RequestMapping("/api/todos") // 设置基础路径
public class TodoController {
    
    @Autowired
    private TodoService todoService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 创建待办项
     * POST /api/todos
     */
    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody Todo todo, @RequestParam Long userId) {
        // 参数验证
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            throw ValidationException.requiredField("标题");
        }
        
        User currentUser = userService.findById(userId);
        Todo createdTodo = todoService.createTodo(todo, currentUser);
        // 转换为DTO返回
        TodoDto todoDto = todoService.convertToDto(createdTodo);
        return ResponseEntity.status(HttpStatus.CREATED).body(todoDto);
    }
    
    /**
     * 获取用户所有待办项
     * GET /api/todos/user/{userId}
     */
    @GetMapping("/user/{userId}")    
    public ResponseEntity<List<TodoDto>> getAllTodosByUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        List<TodoDto> todoDtos = todoService.getAllTodosDtoByUser(user);
        return ResponseEntity.ok(todoDtos);
    }
    
    /**
     * 获取用户主待办项（无父项）
     * GET /api/todos/user/{userId}/main
     */
    @GetMapping("/user/{userId}/main")
    public ResponseEntity<List<TodoDto>> getMainTodosByUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        List<TodoDto> mainTodoDtos = todoService.getMainTodosDtoByUser(user);
        return ResponseEntity.ok(mainTodoDtos);
    }
    
    /**
     * 获取子待办项
     * GET /api/todos/{parentId}/subtodos
     */
    @GetMapping("/{parentId}/subtodos")
    public ResponseEntity<List<TodoDto>> getSubTodos(@PathVariable Long parentId, @RequestParam Long userId) {
        User currentUser = userService.findById(userId);
        Todo parentTodo = todoService.getTodoById(parentId, currentUser);
        List<TodoDto> subTodoDtos = todoService.getSubTodosDtoByParent(parentTodo, currentUser);
        return ResponseEntity.ok(subTodoDtos);
    }
    
    /**
     * 根据ID获取待办项
     * GET /api/todos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodoById(@PathVariable Long id, @RequestParam Long userId) {
        User currentUser = userService.findById(userId);
        TodoDto todoDto = todoService.getTodoDtoById(id, currentUser);
        return ResponseEntity.ok(todoDto);
    }
    
    /**
     * 更新待办项
     * PUT /api/todos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo, @RequestParam Long userId) {
        User currentUser = userService.findById(userId);
        Todo updated = todoService.updateTodo(id, updatedTodo, currentUser);
        // 转换为DTO返回
        TodoDto todoDto = todoService.convertToDto(updated);
        return ResponseEntity.ok(todoDto);
    }
    
    /**
     * 删除待办项
     * DELETE /api/todos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTodo(@PathVariable Long id, @RequestParam Long userId) {
        User currentUser = userService.findById(userId);
        todoService.deleteTodo(id, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
