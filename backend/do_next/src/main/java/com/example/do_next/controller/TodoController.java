package com.example.do_next.controller;

import com.example.do_next.entity.Todo;
import com.example.do_next.entity.User;
import com.example.do_next.service.TodoService;
import com.example.do_next.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo, @RequestParam Long userId) {
        User currentUser = userService.findById(userId);
        Todo createdTodo = todoService.createTodo(todo, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }
    
    /**
     * 获取用户所有待办项
     * GET /api/todos/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Todo>> getAllTodosByUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        List<Todo> todos = todoService.getAllTodosByUser(user);
        return ResponseEntity.ok(todos);
    }
    
    /**
     * 获取用户主待办项（无父项）
     * GET /api/todos/user/{userId}/main
     */
    @GetMapping("/user/{userId}/main")
    public ResponseEntity<List<Todo>> getMainTodosByUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        List<Todo> mainTodos = todoService.getMainTodosByUser(user);
        return ResponseEntity.ok(mainTodos);
    }
    
    /**
     * 根据ID获取待办项
     * GET /api/todos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id, @RequestParam Long userId) {
        User currentUser = userService.findById(userId);
        Todo todo = todoService.getTodoById(id, currentUser);
        return ResponseEntity.ok(todo);
    }
    
    /**
     * 更新待办项
     * PUT /api/todos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo, @RequestParam Long userId) {
        User currentUser = userService.findById(userId);
        Todo updated = todoService.updateTodo(id, updatedTodo, currentUser);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * 删除待办项
     * DELETE /api/todos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id, @RequestParam Long userId) {
        User currentUser = userService.findById(userId);
        todoService.deleteTodo(id, currentUser);
        return ResponseEntity.ok("删除成功");
    }
}
