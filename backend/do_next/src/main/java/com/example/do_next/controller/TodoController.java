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
@CrossOrigin(origins = "*") // 允许跨域请求（开发阶段使用）
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
    public ResponseEntity<?> createTodo(@RequestBody Todo todo, @RequestParam Long userId) {
        try {
            // 获取当前用户（实际项目中应从JWT token或Session中获取）
            User currentUser = userService.findById(userId);
            
            Todo createdTodo = todoService.createTodo(todo, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户所有待办项
     * GET /api/todos/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllTodosByUser(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            List<Todo> todos = todoService.getAllTodosByUser(user);
            return ResponseEntity.ok(todos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户主待办项（无父项）
     * GET /api/todos/user/{userId}/main
     */
    @GetMapping("/user/{userId}/main")
    public ResponseEntity<?> getMainTodosByUser(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            List<Todo> mainTodos = todoService.getMainTodosByUser(user);
            return ResponseEntity.ok(mainTodos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取子待办项
     * GET /api/todos/{parentId}/subtodos
     */
    @GetMapping("/{parentId}/subtodos")
    public ResponseEntity<?> getSubTodos(@PathVariable Long parentId, @RequestParam Long userId) {
        try {
            User currentUser = userService.findById(userId);
            Todo parentTodo = todoService.getTodoById(parentId, currentUser);
            List<Todo> subTodos = todoService.getSubTodosByParent(parentTodo, currentUser);
            return ResponseEntity.ok(subTodos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取待办项
     * GET /api/todos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable Long id, @RequestParam Long userId) {
        try {
            User currentUser = userService.findById(userId);
            Todo todo = todoService.getTodoById(id, currentUser);
            return ResponseEntity.ok(todo);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("无权访问")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问: " + e.getMessage());
            }
            return ResponseEntity.badRequest().body("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新待办项
     * PUT /api/todos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo, @RequestParam Long userId) {
        try {
            User currentUser = userService.findById(userId);
            Todo updated = todoService.updateTodo(id, updatedTodo, currentUser);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("无权访问")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问: " + e.getMessage());
            }
            return ResponseEntity.badRequest().body("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除待办项
     * DELETE /api/todos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id, @RequestParam Long userId) {
        try {
            User currentUser = userService.findById(userId);
            todoService.deleteTodo(id, currentUser);
            return ResponseEntity.ok().body("删除成功");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("无权访问")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问: " + e.getMessage());
            }
            return ResponseEntity.badRequest().body("删除失败: " + e.getMessage());
        }
    }
}
