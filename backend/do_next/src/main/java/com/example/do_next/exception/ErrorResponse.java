package com.example.do_next.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ErrorResponse - 统一错误响应格式类
 * 
 * 作用：
 * 1. 标准化所有API的错误响应格式
 * 2. 提供详细的错误信息给前端
 * 3. 便于前端统一处理错误响应
 * 4. 增强API的可读性和调试能力
 */
@Data // Lombok注解：自动生成getter、setter、toString等方法
@NoArgsConstructor // Lombok注解：生成无参构造函数
@AllArgsConstructor // Lombok注解：生成全参构造函数
public class ErrorResponse {
    
    /**
     * HTTP状态码
     * 例如：400(Bad Request)、404(Not Found)、403(Forbidden)等
     */
    private int status;
    
    /**
     * 错误消息
     * 面向用户的友好错误描述，可以直接显示给用户
     */
    private String message;
    
    /**
     * 错误发生的时间戳
     * 使用@JsonFormat注解格式化JSON输出时间格式
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * 请求路径
     * 发生错误的API端点路径，便于调试和日志记录
     */
    private String path;
    
    /**
     * 错误代码（可选）
     * 内部错误代码，便于开发人员快速定位问题类型
     */
    private String errorCode;
    
    /**
     * 构造函数 - 创建基本错误响应
     * 
     * @param status HTTP状态码
     * @param message 错误消息
     */
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 构造函数 - 创建包含路径的错误响应
     * 
     * @param status HTTP状态码
     * @param message 错误消息
     * @param path 请求路径
     */
    public ErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 构造函数 - 创建包含错误代码的错误响应
     * 
     * @param status HTTP状态码
     * @param message 错误消息
     * @param path 请求路径
     * @param errorCode 错误代码
     */
    public ErrorResponse(int status, String message, String path, String errorCode) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
}
