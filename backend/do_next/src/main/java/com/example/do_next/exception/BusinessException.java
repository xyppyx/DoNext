package com.example.do_next.exception;

/**
 * BusinessException - 业务异常基类
 * 
 * 作用：
 * 1. 作为所有自定义业务异常的父类
 * 2. 区分业务异常和系统异常
 * 3. 提供统一的异常状态码管理
 * 4. 便于全局异常处理器分类处理
 * 
 * 设计原则：
 * - 继承RuntimeException，支持Spring事务回滚
 * - 包含HTTP状态码，便于Controller层响应
 * - 提供多种构造方法，适应不同使用场景
 */
public class BusinessException extends RuntimeException {
    
    /**
     * HTTP状态码
     * 用于Controller层返回相应的HTTP状态
     */
    private final int status;
    
    /**
     * 错误代码（可选）
     * 内部错误分类标识，便于日志分析和问题定位
     */
    private String errorCode;
    
    /**
     * 默认构造函数
     * 使用默认的400状态码（Bad Request）
     * 
     * @param message 错误消息
     */
    public BusinessException(String message) {
        // 调用父类构造函数,作用是初始化错误消息
        super(message);
        this.status = 400; // 默认为Bad Request
    }
    
    /**
     * 带状态码的构造函数
     * 允许指定具体的HTTP状态码
     * 
     * @param status HTTP状态码
     * @param message 错误消息
     */
    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
    }
    
    /**
     * 带错误代码的构造函数
     * 
     * @param status HTTP状态码
     * @param message 错误消息
     * @param errorCode 错误代码
     */
    public BusinessException(int status, String message, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
    
    /**
     * 带原因异常的构造函数
     * 用于包装底层异常，保留异常链
     * 
     * @param status HTTP状态码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public BusinessException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
    
    /**
     * 获取HTTP状态码
     * 
     * @return HTTP状态码
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * 获取错误代码
     * 
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 设置错误代码
     * 
     * @param errorCode 错误代码
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
