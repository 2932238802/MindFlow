# 使用一个合适的 Tomcat 基础镜像
FROM tomcat:9.0-jdk11-temurin

# (可选) 删除默认的 webapps
# RUN rm -rf /usr/local/tomcat/webapps/*

# 复制由 Maven 构建的 WAR 文件 (根据需要调整文件名)
# 假设你在 'backend' 目录下运行 'mvn package' 之后再运行 'docker-compose up --build'
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war
# 使用 ROOT.war 使应用可以通过代理在根路径 '/' 访问

# 暴露 Tomcat 的默认端口 (在 Docker 网络内部)
EXPOSE 8080

# 启动 Tomcat 的默认命令 (通常从基础镜像继承)
# CMD ["catalina.sh", "run"]
