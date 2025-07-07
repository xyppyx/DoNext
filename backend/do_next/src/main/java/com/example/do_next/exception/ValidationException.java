package com.example.do_next.exception;

import java.util.List;
import java.util.Map;

/**
 * ValidationException - 参数验证异常
 * 
 * 业务场景：
 * 1. 用户注册时用户名格式不正确
 * 2. 密码长度不符合要求
 * 3. 待办项标题为空或过长
 * 4. 日期格式错误
 * 5. Bean Validation验证失败
 * 
 * HTTP状态码：400 Bad Request
 * 
 * 使用示例：
 * ```java
 * if (username == null || username.trim().isEmpty()) {
 *     throw new ValidationException("用户名不能为空");
 * }
 * ```
 */
public class ValidationException extends BusinessException {
    
    /**
     * 验证错误详情
     * 键为字段名，值为错误消息列表
     */
    private Map<String, List<String>> validationErrors;
    
    /**
     * 默认构造函数
     * 自动设置400状态码
     * 
     * @param message 验证错误消息
     */
    public ValidationException(String message) {
        super(400, message);
        setErrorCode("VALIDATION_ERROR");
    }
    
    /**
     * 带验证错误详情的构造函数
     * 用于Bean Validation等场景，提供详细的字段错误信息
     * 
     * @param message 总体错误消息
     * @param validationErrors 字段验证错误详情
     */
    public ValidationException(String message, Map<String, List<String>> validationErrors) {
        super(400, message);
        setErrorCode("VALIDATION_ERROR");
        this.validationErrors = validationErrors;
    }
    
    /**
     * 带错误代码的构造函数
     * 
     * @param message 错误消息
     * @param errorCode 具体的验证错误代码
     */
    public ValidationException(String message, String errorCode) {
        super(400, message, errorCode);
    }
    
    /**
     * 获取验证错误详情
     * 
     * @return 验证错误详情Map
     */
    public Map<String, List<String>> getValidationErrors() {
        return validationErrors;
    }
    
    /**
     * 设置验证错误详情
     * 
     * @param validationErrors 验证错误详情
     */
    public void setValidationErrors(Map<String, List<String>> validationErrors) {
        this.validationErrors = validationErrors;
    }
    
    /**
     * 静态工厂方法 - 必填字段为空
     * 
     * @param fieldName 字段名
     * @return ValidationException实例
     */
    public static ValidationException requiredField(String fieldName) {
        return new ValidationException(fieldName + "不能为空", "REQUIRED_FIELD");
    }
    
    /**
     * 静态工厂方法 - 字段长度验证失败
     * 
     * @param fieldName 字段名
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return ValidationException实例
     */
    public static ValidationException invalidLength(String fieldName, int minLength, int maxLength) {
        return new ValidationException(
            fieldName + "长度必须在 " + minLength + " 到 " + maxLength + " 之间",
            "INVALID_LENGTH"
        );
    }
    
    /**
     * 静态工厂方法 - 格式验证失败
     * 
     * @param fieldName 字段名
     * @param expectedFormat 期望格式
     * @return ValidationException实例
     */
    public static ValidationException invalidFormat(String fieldName, String expectedFormat) {
        return new ValidationException(
            fieldName + "格式不正确，期望格式：" + expectedFormat,
            "INVALID_FORMAT"
        );
    }
}
