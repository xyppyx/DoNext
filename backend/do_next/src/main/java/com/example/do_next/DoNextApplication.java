package com.example.do_next;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

/**
 * DoNextApplication - Spring Boot主启动类
 * 
 * 功能说明：
 * 1. 启动Spring Boot应用程序
 * 2. 自动配置Spring Boot的各种功能
 * 3. 扫描并注册所有的Spring组件（Controller、Service、Repository等）
 * 4. 配置必要的Bean（如密码编码器）
 * 5. 配置跨域访问规则
 * 
 * 注解说明：
 * @SpringBootApplication是一个组合注解，包含：
 * - @Configuration: 声明这是一个配置类
 * - @EnableAutoConfiguration: 启用Spring Boot的自动配置机制
 * - @ComponentScan: 自动扫描当前包及子包下的所有Spring组件
 */
@SpringBootApplication
public class DoNextApplication {

	/**
	 * 应用程序入口点
	 * Spring Boot应用的标准启动方法
	 * 
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {
		SpringApplication.run(DoNextApplication.class, args);
		System.out.println("=== DoNext Todo应用启动成功 ===");
		System.out.println("应用访问地址: http://localhost:8080");
		System.out.println("API接口前缀: /api");
		System.out.println("用户接口: /api/users");
		System.out.println("待办事项接口: /api/todos");
	}

	/**
	 * 密码编码器Bean配置
	 * 
	 * 作用：
	 * 1. 用于加密用户密码存储到数据库
	 * 2. 用于验证用户登录时的密码
	 * 3. BCrypt是目前最安全的密码哈希算法之一
	 * 
	 * BCrypt特点：
	 * - 自动生成盐值，每次加密结果都不同
	 * - 计算成本高，有效防止暴力破解
	 * - 单向加密，不可逆转
	 * 
	 * @return BCryptPasswordEncoder实例
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 跨域配置
	 * 
	 * 作用：
	 * 1. 允许前端应用（如React、Vue等）访问后端API
	 * 2. 解决浏览器的同源策略限制
	 * 3. 配置允许的请求方法、头部等
	 * 
	 * 开发环境配置：
	 * - 允许所有源访问（生产环境需要限制具体域名）
	 * - 允许所有HTTP方法
	 * - 允许携带认证信息
	 * 
	 * @return WebMvcConfigurer配置对象
	 */
	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			
			@Override
			public void addCorsMappings(@NonNull CorsRegistry registry) {
				registry.addMapping("/api/**")
						.allowedOriginPatterns("*") // 使用allowedOriginPatterns而不是allowedOrigins
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true) // 这样就可以同时使用了
						.maxAge(3600);
			}
		};
	};
}

