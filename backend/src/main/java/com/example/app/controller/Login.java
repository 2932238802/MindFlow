package com.example.app.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.app.dao.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 监听登录信息
 */
@WebServlet("/api/login")
public class Login extends HttpServlet {

    private static final ObjectMapper objectmapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            handleLogin(request, response);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 私有成员函数 处理登录请求
     * 处理登录请求 然后和数据库验证一下
     * 
     * 这个是请求对象
     * 这个是响应对象
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        // 检查 Content-Type 是否是 application/json
        if (!"application/json".equalsIgnoreCase(request.getContentType())) {
            // 如果请求不是 JSON 格式，直接返回 415
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            response.getWriter().write("{\"message\":\"application/json please\"}");
            return;
        }

        // 读取 JSON 数据
        // 把这个数据存到 StringBuilder jsonBuilder 里面
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            // line 用来保存对应的字符串
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        // 解析 JSON
        LoginRequest loginRequest;
        try {
            // 调试
            System.out.println("Received JSON: " + jsonBuilder.toString());

            loginRequest = objectmapper.readValue(jsonBuilder.toString(), LoginRequest.class);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Invalid JSON Data.\"}");
            return;
        }

        // 获取登录信息
        String user_name = loginRequest.getUser_name();
        String password = loginRequest.getPassword();

        Dao da = new Dao();

        int user_id = da.checkUser(user_name, password);

        if (user_id!=-1) {
            jakarta.servlet.http.HttpSession session = request.getSession(true); // true 表示没有session 就创建
            
            // 设置一下会话信息 方便之后读取
            session.setAttribute("user_name", user_name);
            session.setAttribute("user_id", user_id);

            // 
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");

            // TODO: 前端
            response.getWriter().write("{\"message\":\"Login successful!\"}");

        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Login failed Incorrect username or password\"}");
            System.out.println("Login failed! User not found");
        }

    }

    
    private static class LoginRequest {
        private String user_name;
        private String password;

        public String getUser_name() { 
            return user_name;
        }

        public String getPassword() {
            return password;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
