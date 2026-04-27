# 基础镜像：使用轻量级的 OpenJDK 17 或 21 (根据你实际的 Java 版本修改)
FROM openjdk:21-jdk-alpine

# 设定工作目录
WORKDIR /app

# 声明对外暴露的端口 (比如 user-service 是 8081)
EXPOSE 8081

# 将 target 目录下打包好的 jar 包复制到容器内，并重命名为 app.jar
# (注意：执行 docker build 前，你需要先在 IDEA 里执行 maven package 打包出 jar 包)
COPY target/*.jar app.jar

# 设定时区为上海 (企业开发极易踩坑的地方，不加这个查出来的数据库时间少 8 小时)
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 容器启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]