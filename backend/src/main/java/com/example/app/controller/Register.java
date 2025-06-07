package com.example.app.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import com.example.app.util.PasswordEncrypt;

import java.io.IOException;

import com.example.app.dao.Dao;

/**
 * 监听注册信息
 * 它监听 /api/register 路径 (相对于应用的上下文根)
 */
@WebServlet("/api/register")
public class Register extends HttpServlet {

    private static final Gson gson = new Gson();


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求 URL 路径 //
        String path = request.getRequestURI();

        // 如果路径是 /register //
        if (path.endsWith("/register")) {
            try {
                HandleRegister(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{f\"message\":\"处理失败,发生错误\"}");
            }
        }
    }

    /**
     * 为用户注册处理逻辑
     * 请求对象
     * 响应对象
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
            registerRequest = gson.fromJson(jsonbuilder.toString(), RegisterRequest.class);
        } 
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{f\"message\":\"无效的json信息\"}");
            return;
        }

        // 获取注册信息
        Dao dao = new Dao();
        String user_name = registerRequest.getUser_name();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();
        String encrypted_password = PasswordEncrypt.encrypt(password);

        //添加童虎
        boolean flag = dao.addUser(user_name, encrypted_password, email);

        if (flag == false) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"账号或者邮箱已经存在\"}");
            return;
        }
        else{
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"登录成功\"}");
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
