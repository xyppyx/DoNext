package com.example.do_next.exception;

/**
 * AccessDeniedException - 访问权限异常
 * 
 * 业务场景：
 * 1. 用户尝试访问不属于自己的待办项
 * 2. 用户尝试修改他人的数据
 * 3. 用户权限不足，无法执行某些操作
 * 4. 资源的所有权验证失败
 * 
 * HTTP状态码：403 Forbidden
 * 
 * 使用示例：
 * ```java
 * if (!todo.getUser().getUserId().equals(currentUser.getUserId())) {
 *     throw new AccessDeniedException("无权访问此待办项");
 * }
 * ```
 */
public class AccessDeniedException extends BusinessException {
    
    /**
     * 默认构造函数
     * 自动设置403状态码
     * 
     * @param message 错误消息，描述为什么访问被拒绝
     */
    public AccessDeniedException(String message) {
        super(403, message);
        setErrorCode("ACCESS_DENIED");
    }
    
    /**
     * 带错误代码的构造函数
     * 允许指定具体的权限错误类型
     * 
     * @param message 错误消息
     * @param errorCode 具体的错误代码
     */
    public AccessDeniedException(String message, String errorCode) {
        super(403, message, errorCode);
    }
    
    /**
     * 带原因异常的构造函数
     * 用于包装安全框架异常等
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(403, message, cause);
        setErrorCode("ACCESS_DENIED");
    }
    
    /**
     * 静态工厂方法 - 数据所有权验证失败
     * 用于待办项、用户数据等所有权检查失败的场景
     * 
     * @param resourceType 资源类型（如"待办项"、"用户信息"）
     * @return AccessDeniedException实例
     */
    public static AccessDeniedException ownershipViolation(String resourceType) {
        return new AccessDeniedException("无权访问此" + resourceType, "OWNERSHIP_VIOLATION");
    }
    
    /**
     * 静态工厂方法 - 角色权限不足
     * 用于基于角色的权限控制场景
     * 
     * @param requiredRole 需要的角色
     * @param currentRole 当前用户角色
     * @return AccessDeniedException实例
     */
    public static AccessDeniedException insufficientRole(String requiredRole, String currentRole) {
        return new AccessDeniedException(
            "权限不足，需要 " + requiredRole + " 角色，当前角色为 " + currentRole, 
            "INSUFFICIENT_ROLE"
        );
    }
    
    /**
     * 静态工厂方法 - 操作权限不足
     * 用于特定操作的权限检查
     * 
     * @param operation 操作名称（如"删除"、"修改"）
     * @param resourceType 资源类型
     * @return AccessDeniedException实例
     */
    public static AccessDeniedException operationDenied(String operation, String resourceType) {
        return new AccessDeniedException(
            "无权" + operation + "此" + resourceType, 
            "OPERATION_DENIED"
        );
    }
}
