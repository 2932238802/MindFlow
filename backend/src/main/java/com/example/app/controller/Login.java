package com.example.app.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.app.util.PasswordEncrypt;
import com.example.app.util.DB;

import com.fasterxml.jackson.databind.ObjectMapper; 

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 监听登录信息
 * 它监听 /api/login 路径 (相对于应用的上下文根)
 */
@WebServlet("/api/login")
public class Login extends HttpServlet {

    private static final ObjectMapper objectmapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // String path = request.getRequestURI();
        handleLogin(request, response);
    }

    /**
     * 私有成员函数 处理登录请求
     * 处理登录请求 然后和数据库验证一下
     * 
     * @param request  这个是请求对象
     * @param response 这个是响应对象
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 检查 Content-Type 是否是 application/json
        if (!"application/json".equalsIgnoreCase(request.getContentType())) {
            // 如果请求不是 JSON 格式，直接返回 415
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            response.getWriter().write("{\"message\":\"application/json please\"}");
            return;
        }

        // 读取 JSON 数据 //
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        // 解析 JSON //
        LoginRequest loginRequest;
        try {
            // 调试 //
            System.out.println("Received JSON: " + jsonBuilder.toString());

            loginRequest = objectmapper.readValue(jsonBuilder.toString(), LoginRequest.class);
        }
        catch (Exception e) {
            //  //
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Invalid JSON Data.\"}");
            return;
        }

        // 获取登录信息
        String user_name = loginRequest.getUser_name();
        String password = loginRequest.getPassword();

        // 根据数据库处理 信息是不是正确 //
        // 使用 try-with-resources 自动管理资源
        try (
                // 获取数据库连接 //
                Connection connect = DB.getConnection();

                // 创建一个查询的语句 //
                PreparedStatement prep = connect.prepareStatement(
                        "SELECT * FROM users WHERE user_name = ?")
        ) {
            // 为 SQL 查询中的占位符绑定参数
            // 第一个问号绑定用户名参数 (1 表示第一个问号)
            // 第二个问号绑定用户密码参数 (2 表示第二个问号)
            prep.setString(1, user_name);

            // Debug //
            System.out.println("Executing SQL query: SELECT * FROM users WHERE user_name = ?");
            System.out.println("Parameters: user_name=" + user_name);

            // 执行查询操作 并使用 try-with-resources 确保 ResultSet 自动关闭 //
            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    // 获得mysql的数据 //
                    String hashedPassword = result.getString("password");

                    // 获取唯一 user_id
                    int user_id = result.getInt("user_id");

                    // 验证一下 //
                    if (PasswordEncrypt.verify(password, hashedPassword)) {
                        // 保存到会话里面
                        jakarta.servlet.http.HttpSession session =request.getSession(true);   // true 表示没有session 就创建
                        session.setAttribute("user_id", user_id);

                        response.setStatus(HttpServletResponse.SC_CREATED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\":\"Login successful!\"}");
                        System.out.println("Login successful!User ID: " + result.getInt("user_id"));
                    }
                    else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\":\"Login failed Incorrect username or password\"}");
                        System.out.println("Login failed Incorrect username or password");
                    }
                }
                else {
                    // 如果用户名不存在
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Login failed Incorrect username or password\"}");
                    System.out.println("Login failed! User not found");
                }
            }
        } catch (SQLException e) {
            // 捕获 SQL 异常并返回 HTTP 500 状态 //
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"An internal server error occurred\"}");
        }
    }

    /**
     * 内部类，用于解析 JSON 数据
     * 定义用户提交的登录信息 (user_name, password, remember)
     */
    private static class LoginRequest {
        // 定义字段
        private String user_name;
        private String password;
        // Getter 和 Setter 方法，采用符合 Java Bean 规范的命名方式

        public String getUser_name() { // Getter 方法
            return user_name;
        }

        public String getPassword() { // Getter 方法
            return password;
        }

    }


    @Override
    public void destroy() {
    super.destroy();
  }

}
