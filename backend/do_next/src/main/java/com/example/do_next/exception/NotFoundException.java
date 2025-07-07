package com.example.do_next.exception;

/**
 * NotFoundException - 资源不存在异常
 * 
 * 业务场景：
 * 1. 根据ID查找用户，但用户不存在
 * 2. 根据ID查找待办项，但待办项不存在
 * 3. 查找指定条件的数据，但数据库中没有匹配记录
 * 4. 访问已被删除的资源
 * 
 * HTTP状态码：404 Not Found
 * 
 * 使用示例：
 * ```java
 * if (todo.isEmpty()) {
 *     throw new NotFoundException("ID为 " + id + " 的待办项不存在");
 * }
 * ```
 */
public class NotFoundException extends BusinessException {
    
    /**
     * 默认构造函数
     * 自动设置404状态码
     * 
     * @param message 错误消息，描述哪个资源不存在
     */
    public NotFoundException(String message) {
        super(404, message);
        setErrorCode("RESOURCE_NOT_FOUND");
    }
    
    /**
     * 带错误代码的构造函数
     * 允许指定具体的错误分类代码
     * 
     * @param message 错误消息
     * @param errorCode 具体的错误代码
     */
    public NotFoundException(String message, String errorCode) {
        super(404, message, errorCode);
    }
    
    /**
     * 带原因异常的构造函数
     * 用于包装数据库异常等底层异常
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public NotFoundException(String message, Throwable cause) {
        super(404, message, cause);
        setErrorCode("RESOURCE_NOT_FOUND");
    }
    
    /**
     * 静态工厂方法 - 用户不存在
     * 提供语义化的异常创建方法
     * 
     * @param userId 用户ID
     * @return NotFoundException实例
     */
    public static NotFoundException userNotFound(Long userId) {
        return new NotFoundException("ID为 " + userId + " 的用户不存在", "USER_NOT_FOUND");
    }
    
    /**
     * 静态工厂方法 - 用户名不存在
     * 
     * @param username 用户名
     * @return NotFoundException实例
     */
    public static NotFoundException userNotFound(String username) {
        return new NotFoundException("用户名 '" + username + "' 不存在", "USER_NOT_FOUND");
    }
    
    /**
     * 静态工厂方法 - 待办项不存在
     * 
     * @param todoId 待办项ID
     * @return NotFoundException实例
     */
    public static NotFoundException todoNotFound(Long todoId) {
        return new NotFoundException("ID为 " + todoId + " 的待办项不存在", "TODO_NOT_FOUND");
    }
}
