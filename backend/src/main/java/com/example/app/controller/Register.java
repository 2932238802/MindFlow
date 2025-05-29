package com.example.app.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.ObjectMapper; 

import com.example.app.util.PasswordEncrypt;
import com.example.app.util.DB;

/**
 * 监听注册信息
 * 它监听 /api/register 路径 (相对于应用的上下文根)
 */
@WebServlet("/api/register")
public class Register extends HttpServlet {

    // 定义一个 ObjectMapper 实例，用于解析 JSON 数据 (Jackson 库)
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求 URL 路径 //
        String path = request.getRequestURI();

        // 如果路径是 /register //
        if (path.endsWith("/register")) {
            try {
                HandleRegister(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * 为用户注册处理逻辑
     *
     * @param request  请求对象
     * @param response 响应对象
     */
    private void HandleRegister(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 检查 Content-Type 是否是 application/json
        if (!"application/json".equalsIgnoreCase(request.getContentType())) {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            response.setContentType("application/json");
            return;
        }

        // 读取 JSON 数据 //
        StringBuilder jsonbuilder = new StringBuilder();
        try (var reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonbuilder.append(line);
            }
        }

        // 解析 JSON
        RegisterRequest registerRequest;
        try {
            // 使用 ObjectMapper 将 JSON 数据反序列化为 Java 对象
            registerRequest = objectMapper.readValue(jsonbuilder.toString(), RegisterRequest.class);
        } catch (Exception e) {
            // 如果解析 JSON 失败，返回 400 Bad Request
            // 
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid JSON data.\"}");
            return;
        }

        // 获取注册信息 //
        String user_name = registerRequest.getUser_name();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();


        try (
                // 连接数据库 //
                Connection connection = DB.getConnection();

                PreparedStatement prep_check = connection.prepareStatement(
                        "SELECT COUNT(*) AS count FROM users WHERE user_name = ? OR email = ?");

                PreparedStatement prep_insert = connection.prepareStatement(
                        "INSERT INTO users (user_name, password, email) VALUES (?,?,?)")
        ) {
            // 检查是否重复 //
            prep_check.setString(1, user_name);
            prep_check.setString(2, email);

            try (ResultSet result = prep_check.executeQuery()) {
                if (result.next() && result.getInt("count") > 0) {
                    // 如果重复，返回错误 //
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Username or email already exists.\"}");
                    return;
                }
            }

            // 插入新数据
            String encrypted_password = PasswordEncrypt.encrypt(password);

            prep_insert.setString(1, user_name);
            prep_insert.setString(2, encrypted_password);
            prep_insert.setString(3, email);

            // executeUpdate() 是 PreparedStatement 对象的一个方法
            // 它执行的是与数据库更新有关的 SQL 语句，例如 INSERT、UPDATE 和 DELETE
            // 并返回被影响的行数（即完成操作后数据库发生更改的记录数）
            int rows_affected = prep_insert.executeUpdate();

            if (rows_affected > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
        }
    }

    /**
     * 内部类，用于解析 JSON 数据
     * 定义用户提交的注册信息 (user_name, email, password)
     */
    private static class RegisterRequest {
        private String user_name;
        private String email;
        private String password;

        // Getter 和 Setter 方法，确保 Jackson 可以正常解析
        public String getUser_name() {
            return user_name;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

    }
}
