# Dockerfile
# 基于 Eclipse Temurin OpenJDK 21
FROM eclipse-temurin:21-jdk

# 设置容器内的工作目录
# 这是应用文件将要存放的地方
WORKDIR /opt/app

# 将你的 Spring Boot JAR 文件复制到容器的 /opt/app 目录下
# 这里的 'do_next-0.0.1-SNAPSHOT.jar' 是你在云服务器上上传的 JAR 文件名
# 'app.jar' 是容器内该文件的名称，保持一致性
COPY do_next-0.0.1-SNAPSHOT.jar /opt/app/app.jar

# 暴露你的 Spring Boot 应用监听的端口
# 默认是 8080，请根据你的 application.properties 配置进行确认
EXPOSE 8080

# 定义容器启动时执行的命令
# 运行复制到 /opt/app 目录下的 JAR 包
# 注意：官方文档用的是 CMD，但我更推荐 ENTRYPOINT，因为它更符合应用程序启动的语义，
# 且能确保 Java 进程是容器的PID 1，便于信号处理。
# 如果你严格遵循官方文档的 CMD 示例，也可以使用：
# CMD ["java", "-jar", "/opt/app/app.jar"]
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]

# 如果你想为 JVM 设置更多的内存参数，可以在 ENTRYPOINT 中添加，例如：
# ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "/opt/app/app.jar"]