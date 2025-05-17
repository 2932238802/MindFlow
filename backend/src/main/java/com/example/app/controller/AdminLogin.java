
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            AdminLoginRequest loginPayload = objectMapper.readValue(request.getInputStream(), AdminLoginRequest.class);

            String username = loginPayload.getUser_name();
            String password = loginPayload.getPassword();

            if ("2932238802".equals(username) && "2932238802".equals(password)) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put("message", "登录成功");
                Map<String, String> userData = new HashMap<>();
                userData.put("username", username);
                jsonResponse.put("user", userData);
                
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.put("message", "用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("message", "请求格式错误 或者 服务器内部错误");
        }
        
        // Debug
        out.print(objectMapper.writeValueAsString(jsonResponse));
        out.flush();
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}