package com.example.do_next.service;

import com.example.do_next.dto.UserDto;
import com.example.do_next.entity.User;
import com.example.do_next.exception.BusinessException;
import com.example.do_next.exception.NotFoundException;
import com.example.do_next.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * UserService - 用户管理业务逻辑服务类
 * 
 * 核心职责：
 * 1. 用户注册：检查用户名唯一性、密码加密、角色分配
 * 2. 用户认证：验证用户名密码的正确性
 * 3. 用户查询：根据ID或用户名查找用户
 * 4. 安全管理：密码加密存储，防止明文泄露
 */
@Service // Spring注解：标识这是一个服务层组件，由Spring容器管理
public class UserService {
    
    /**
     * @Autowired注解：自动依赖注入UserRepository
     * UserRepository负责与数据库的用户表进行交互
     */
    @Autowired
    private UserRepository userRepository;
    
    /**
     * @Autowired注解：自动依赖注入密码编码器
     * PasswordEncoder是Spring Security提供的密码加密接口
     * 通常使用BCryptPasswordEncoder实现，提供安全的密码哈希
     */
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 注册用户
     * 
     * 业务逻辑：
     * 1. 验证用户名唯一性
     * 2. 加密用户密码
     * 3. 分配默认角色
     * 4. 保存到数据库
     * 
     * @param userName 用户名
     * @param rawPassword 明文密码
     * @return 注册成功的用户对象
     * @throws RuntimeException 如果用户名已存在
     */
    @Transactional
    public User registerUser(String userName, String rawPassword) {
        // 业务验证：检查用户名是否已存在
        // findByUserName返回Optional，避免空指针异常
        if (userRepository.findByUserName(userName).isPresent()) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建新用户对象
        User user = new User();
        user.setUserName(userName);
        
        // 安全处理：使用PasswordEncoder加密密码
        // 永远不要在数据库中存储明文密码
        // BCrypt算法会自动生成盐值，每次加密结果都不同，但验证时能正确匹配
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        
        // 业务规则：分配默认角色
        user.setUserRole("USER");
        
        // JPA操作：保存用户到数据库
        // @PrePersist会自动设置创建时间和更新时间
        return userRepository.save(user);
    }
    
    /**
     * 用户认证
     * 
     * 业务逻辑：
     * 1. 根据用户名查找用户
     * 2. 使用PasswordEncoder验证密码
     * 
     * @param userName 用户名
     * @param rawPassword 明文密码
     * @return 认证结果：true表示成功，false表示失败
     */
    @Transactional
    public boolean authenticateUser(String userName, String rawPassword) {
        // JPA查询：根据用户名查找用户
        Optional<User> userOpt = userRepository.findByUserName(userName);
        
        // 验证：用户是否存在
        if (userOpt.isEmpty()) {
            return false; // 用户不存在，认证失败
        }
        
        User user = userOpt.get();
        boolean isMatch = passwordEncoder.matches(rawPassword, user.getPassword());

        if(isMatch) {
            // 认证成功后，可以更新用户的最后登录时间
            user.setLastLoginAt(java.time.LocalDateTime.now());
            userRepository.save(user); // 保存更新后的用户信息
        }
        
        // 安全验证：使用PasswordEncoder比较密码
        // matches方法会将明文密码与数据库中的加密密码进行比较
        // 内部会使用相同的盐值和算法进行验证
        return isMatch;
    }
    
    /**
     * 根据用户名查找用户
     * 
     * 业务场景：用于用户登录后获取用户信息，或在业务逻辑中查找用户
     * 
     * @param userName 用户名
     * @return 用户对象
     * @throws RuntimeException 如果用户不存在
     */
    public User findByUserName(String userName) {
        // JPA查询 + 异常处理：查找用户，不存在则抛出异常
        // orElseThrow是Optional的方法，当值不存在时执行lambda表达式
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> NotFoundException.userNotFound(userName));
    }
    
    /**
     * 根据ID查找用户
     * 
     * 业务场景：根据主键快速查找用户，通常用于权限验证或关联查询
     * 
     * @param id 用户ID
     * @return 用户对象
     * @throws RuntimeException 如果用户不存在
     */
    public User findById(Long id) {
        // JPA操作：根据主键查找，这是最高效的查询方式
        return userRepository.findById(id)
                .orElseThrow(() -> NotFoundException.userNotFound(id));
    }
    
    /**
     * 检查用户名是否存在
     * 
     * 业务场景：
     * 1. 注册时的用户名唯一性检查
     * 2. 前端实时验证用户名可用性
     * 
     * @param userName 用户名
     * @return true表示用户名已存在，false表示可用
     */
    public boolean existsByUserName(String userName) {
        // 简洁的存在性检查：只关心是否存在，不需要获取完整对象
        return userRepository.findByUserName(userName).isPresent();
    }
    
    /**
     * 将User实体转换为UserDto
     * 
     * @param user 用户实体
     * @return 用户DTO
     */
    public UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setUserRole(user.getUserRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }
    
    /**
     * 根据ID查找用户并返回DTO
     * 
     * @param id 用户ID
     * @return 用户DTO
     */
    public UserDto findUserDtoById(Long id) {
        User user = findById(id);
        return convertToDto(user);
    }
    
    /**
     * 根据用户名查找用户并返回DTO
     * 
     * @param username 用户名
     * @return 用户DTO
     */
    public UserDto findUserDtoByUsername(String username) {
        User user = findByUserName(username);
        return convertToDto(user);
    }
}
