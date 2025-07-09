# Dockerfile
# 使用一个包含 JDK 21 的 OpenJDK 镜像作为基础镜像
# 推荐使用 Eclipse Adoptium 的 Temurin 项目提供的镜像，它们是免费且稳定的 OpenJDK 发行版
# 'alpine' 版本通常更小，适合生产环境，但可能需要额外安装一些工具
# 'slim' 版本比 alpine 稍大，但提供了更完整的环境
FROM eclipse-temurin:21-jre-alpine

# 设置作者或维护者信息（可选）
LABEL maintainer="Xu YunPeng <2353583@tongji.edu.cn>"

# 设置容器内的工作目录
# 这是应用文件将要存放的地方
WORKDIR /app

# 将 Maven 打包生成的 JAR 文件复制到镜像中
# 假设你的 JAR 文件名为 do_next-0.0.1-SNAPSHOT.jar
# 请根据你实际生成的 JAR 文件名进行调整！
# 你可以通过运行 `ls target/*.jar` 或 `dir target\*.jar` 来确认文件名
ARG JAR_FILE=target/do_next-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 暴露你的 Spring Boot 应用监听的端口
# 默认是 8080，请根据你的 application.properties 配置进行确认
EXPOSE 8080

# 定义容器启动时执行的命令
# `-Dspring.profiles.active=prod` 可以指定生产环境配置文件，如果需要
# `-jar app.jar` 运行复制到 /app 目录下的 JAR 包
ENTRYPOINT ["java", "-jar", "app.jar"]

# 如果你想为 JVM 设置更多的内存参数，可以在这里添加，例如：
# ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]