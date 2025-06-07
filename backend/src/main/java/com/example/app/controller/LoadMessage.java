package com.example.app.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.example.app.dao.Dao;
import com.example.app.model.Email;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; 

@WebServlet("/api/loadmessages")
public class LoadMessage extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        Dao dao = new Dao();

        try {
            // 1. 从会话中读取id
            HttpSession session = request.getSession(false); 
            Integer user_id = null;

            if (session != null && session.getAttribute("user_id") != null) { 
                user_id = (Integer) session.getAttribute("user_id");
            }

            if (user_id == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 未授权
                JsonObject error = new JsonObject();
                error.addProperty("error", "用户未登录或会话无效。");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            // 2. 调用 DAO 获取邮件
            List<Email> emails = dao.getAllMessage(user_id); 

            // 3. 转换成 JSON 格式并发送响应
            String json_response = gson.toJson(emails);
            response.getWriter().write(json_response);

        } catch (SQLException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "数据库操作失败: " + e.getMessage());
            response.getWriter().write(gson.toJson(error));
        } 
        catch (Exception e) { 
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "处理请求时发生未知错误: " + e.getMessage());
            response.getWriter().write(gson.toJson(error));
        }
    }
}
