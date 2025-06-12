
package com.example.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 1. 封装一个类 用户登陆的类
 */
class AdminLoginRequest {
    private String user_name;
    private String password;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

@WebServlet("/api/adminlogin")
public class AdminLogin extends HttpServlet {
    /**
     * 1. 管理员登录界面
     * 2. 处理管理员请求
     */

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

                // 回复设置
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 获得回复的执笔人
        PrintWriter respons_writer = response.getWriter();
        Map<String, Object> json_response = new HashMap<>();

        try {
            // 创建一个 AdminLoginRequest 管理员请求类
            AdminLoginRequest loginPayload = objectMapper.readValue(request.getInputStream(), AdminLoginRequest.class);

            // 把请求里面
            String username = loginPayload.getUser_name();
            String password = loginPayload.getPassword();

            if ("2932238802".equals(username) && "2932238802".equals(password)) {
                response.setStatus(HttpServletResponse.SC_OK);
                json_response.put("message", "登录成功");
                Map<String, String> userData = new HashMap<>();

                userData.put("username", username);
                json_response.put("user", userData);
                
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                json_response.put("message", "用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json_response.put("message", "请求格式错误 或者 服务器内部错误");
        }
        
        
        // 回复顺便 清空一下缓冲区
        respons_writer.print(objectMapper.writeValueAsString(json_response));
        respons_writer.flush();
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}