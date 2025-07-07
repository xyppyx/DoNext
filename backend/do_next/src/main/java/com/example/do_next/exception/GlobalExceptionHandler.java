package com.example.do_next.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler - 全局异常处理器
 * 
 * 作用：
 * 1. 统一处理所有Controller抛出的异常
 * 2. 将异常转换为标准化的错误响应格式
 * 3. 避免在Controller中编写重复的try-catch代码
 * 4. 提供统一的日志记录和错误响应
 * 5. 增强系统的可维护性和一致性
 * 
 * 注解说明：
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * 自动将返回值转换为JSON格式
 */
@RestControllerAdvice // 全局控制器增强，自动返回JSON响应
@Slf4j // Lombok注解，自动生成log对象用于日志记录
public class GlobalExceptionHandler {
    
    /**
     * 处理资源不存在异常
     * 对应HTTP 404状态码
     * 
     * @param e NotFoundException实例
     * @param request HTTP请求对象
     * @return 404错误响应
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException e, HttpServletRequest request) {
        
        // 记录警告级别日志，资源不存在通常不是系统错误
        log.warn("资源不存在异常: {} - 请求路径: {}", e.getMessage(), request.getRequestURI());
        
        // 创建错误响应
        ErrorResponse errorResponse = new ErrorResponse(
            e.getStatus(),
            e.getMessage(),
            request.getRequestURI(),
            e.getErrorCode()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * 处理访问权限异常
     * 对应HTTP 403状态码
     * 
     * @param e AccessDeniedException实例
     * @param request HTTP请求对象
     * @return 403错误响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        
        // 记录警告级别日志，权限问题需要关注但不是系统错误
        log.warn("访问权限异常: {} - 请求路径: {} - 用户IP: {}", 
                e.getMessage(), request.getRequestURI(), getClientIP(request));
        
        ErrorResponse errorResponse = new ErrorResponse(
            e.getStatus(),
            e.getMessage(),
            request.getRequestURI(),
            e.getErrorCode()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    /**
     * 处理参数验证异常
     * 对应HTTP 400状态码
     * 
     * @param e ValidationException实例
     * @param request HTTP请求对象
     * @return 400错误响应
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException e, HttpServletRequest request) {
        
        log.warn("参数验证异常: {} - 请求路径: {}", e.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = new ErrorResponse(
            e.getStatus(),
            e.getMessage(),
            request.getRequestURI(),
            e.getErrorCode()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * 处理通用业务异常
     * 对应业务异常中指定的HTTP状态码
     * 
     * @param e BusinessException实例
     * @param request HTTP请求对象
     * @return 相应状态码的错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e, HttpServletRequest request) {
        
        log.warn("业务异常: {} - 请求路径: {}", e.getMessage(), request.getRequestURI());
        
        ErrorResponse errorResponse = new ErrorResponse(
            e.getStatus(),
            e.getMessage(),
            request.getRequestURI(),
            e.getErrorCode()
        );
        
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }
    
    /**
     * 处理Bean Validation验证失败异常
     * 当使用@Valid注解验证请求体时触发
     * 
     * @param e MethodArgumentNotValidException实例
     * @param request HTTP请求对象
     * @return 400错误响应，包含详细的字段验证错误
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        
        log.warn("Bean Validation验证失败 - 请求路径: {}", request.getRequestURI());
        
        // 提取字段验证错误详情
        Map<String, List<String>> validationErrors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            
            // 修复潜在的空指针问题
            validationErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });
        
        // 构建友好的错误消息
        String message = "参数验证失败: " + validationErrors.entrySet().stream()
            .map(entry -> entry.getKey() + "(" + String.join(", ", entry.getValue()) + ")")
            .collect(Collectors.joining("; "));
        
        ErrorResponse errorResponse = new ErrorResponse(
            400,
            message,
            request.getRequestURI(),
            "VALIDATION_ERROR"
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * 处理参数类型转换异常
     * 当URL参数类型不匹配时触发（如传入字符串给Long类型参数）
     * 
     * @param e MethodArgumentTypeMismatchException实例
     * @param request HTTP请求对象
     * @return 400错误响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        
        log.warn("参数类型转换异常: 参数 {} 的值 {} 无法转换为 {} - 请求路径: {}", 
                e.getName(), e.getValue(), 
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型", 
                request.getRequestURI());
        
        String typeName = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型";
        String message = String.format("参数 '%s' 的值 '%s' 格式不正确，期望类型为 %s", 
                e.getName(), e.getValue(), typeName);
        
        ErrorResponse errorResponse = new ErrorResponse(
            400,
            message,
            request.getRequestURI(),
            "PARAMETER_TYPE_MISMATCH"
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * 处理所有未被上述方法捕获的异常
     * 作为兜底的异常处理机制
     * 
     * @param e Exception实例
     * @param request HTTP请求对象
     * @return 500错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception e, HttpServletRequest request) {
        
        // 记录错误级别日志，系统异常需要重点关注
        log.error("系统异常 - 请求路径: {} - 异常信息: {}", request.getRequestURI(), e.getMessage(), e);
        
        ErrorResponse errorResponse = new ErrorResponse(
            500,
            "系统内部错误，请稍后重试",
            request.getRequestURI(),
            "INTERNAL_SERVER_ERROR"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * 获取客户端真实IP地址
     * 考虑代理服务器和负载均衡的情况
     * 
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}
