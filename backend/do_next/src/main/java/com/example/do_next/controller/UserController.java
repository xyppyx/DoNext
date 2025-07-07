package com.example.do_next.controller;

import com.example.do_next.entity.User;
import com.example.do_next.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * UserController - 用户控制器
 * 
 * 职责：
 * 1. 处理用户注册、登录相关的HTTP请求
 * 2. 用户信息查询和管理
 * 3. 参数验证和响应格式化
 * 4. 异常处理和错误响应
 */
@RestController // 组合注解：@Controller + @ResponseBody，返回JSON格式数据
@RequestMapping("/api/users") // 设置基础路径
@CrossOrigin(origins = "*") // 允许跨域请求（开发阶段使用，生产环境需要配置具体域名）
public class UserController {
    
    /**
     * @Autowired注解：自动依赖注入UserService
     * Spring容器会自动查找UserService类型的Bean并注入
     */
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     * POST /api/users/register
     * 
     * @param registerRequest 包含用户名和密码的请求体
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registerRequest) {
        try {
            // 从请求体中提取参数
            String username = registerRequest.get("username");
            String password = registerRequest.get("password");
            
            // 基础参数验证
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("用户名不能为空");
            }
            if (password == null || password.length() < 6) {
                return ResponseEntity.badRequest().body("密码长度不能少于6位");
            }
            
            // 调用Service层进行业务处理
            User newUser = userService.registerUser(username.trim(), password);
            
            // 构造成功响应（不返回密码等敏感信息）
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("userId", newUser.getUserId());
            response.put("username", newUser.getUserName());
            response.put("userRole", newUser.getUserRole());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            // 处理业务异常（如用户名已存在）
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 用户登录
     * POST /api/users/login
     * 
     * @param loginRequest 包含用户名和密码的请求体
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        try {
            // 从请求体中提取参数
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            // 基础参数验证
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("用户名不能为空");
            }
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body("密码不能为空");
            }
            
            // 调用Service层进行认证
            boolean isAuthenticated = userService.authenticateUser(username.trim(), password);
            
            if (isAuthenticated) {
                // 认证成功，获取用户信息
                User user = userService.findByUserName(username.trim());
                
                // 构造成功响应
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "登录成功");
                response.put("userId", user.getUserId());
                response.put("username", user.getUserName());
                response.put("userRole", user.getUserRole());
                response.put("email", user.getEmail());
                
                return ResponseEntity.ok(response);
            } else {
                // 认证失败
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户名或密码错误");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
        } catch (RuntimeException e) {
            // 处理其他异常
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "登录失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 检查用户名是否可用
     * GET /api/users/check-username?username=xxx
     * 
     * @param username 要检查的用户名
     * @return 检查结果
     */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        try {
            // 参数验证
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("用户名不能为空");
            }
            
            // 检查用户名是否已存在
            boolean exists = userService.existsByUserName(username.trim());
            
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("available", !exists);
            response.put("message", exists ? "用户名已存在" : "用户名可用");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "检查失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 根据ID获取用户信息
     * GET /api/users/{id}
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            // 调用Service层查找用户
            User user = userService.findById(id);
            
            // 构造响应（过滤敏感信息）
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("username", user.getUserName());
            response.put("email", user.getEmail());
            response.put("userRole", user.getUserRole());
            response.put("createdAt", user.getCreatedAt());
            response.put("lastLoginAt", user.getLastLoginAt());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseEntity.notFound().build();
            }
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取用户信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 根据用户名获取用户信息
     * GET /api/users/by-username/{username}
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            // 调用Service层查找用户
            User user = userService.findByUserName(username);
            
            // 构造响应（过滤敏感信息）
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("username", user.getUserName());
            response.put("email", user.getEmail());
            response.put("userRole", user.getUserRole());
            response.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("不存在")) {
                return ResponseEntity.notFound().build();
            }
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取用户信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 用户登出（简单实现）
     * POST /api/users/logout
     * 
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // 简单的登出响应（实际项目中可能需要清除JWT token、Session等）
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登出成功");
        
        return ResponseEntity.ok(response);
    }
}
