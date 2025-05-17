package com.example.app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import com.example.app.dao.Dao;
import com.example.app.model.Task;
import com.example.app.util.GetId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



/**
 * 1. AddTask 添加任务的类
 * 2. doPost 处理post请求
 * 主要的代码逻辑: 设置response内容格式
 * 
 * 
 * 
 * 
 * 
 * 
 */




@WebServlet("/api/addtask")
public class AddTask extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        Gson gson = new Gson();
        Dao taskdao = new Dao();
        StringBuilder sb = new StringBuilder();

        // 读取请求体内容
        String line;
        try (BufferedReader reader = request.getReader()) { 
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String requestBody = sb.toString();
        Task taskWithoutId = gson.fromJson(requestBody, Task.class);
        String generatedId = GetId.getUuid();
        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("user_id") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\":\"please auth first!\"}");
            return;
        }

        int user_id = (int) session.getAttribute("user_id");

        Task task = new Task();
        task.setId(generatedId);
        task.setName(taskWithoutId.getName());
        task.setIsdelete(taskWithoutId.getIsdelete());
        task.setTime(taskWithoutId.getTime());
        task.setNotified(taskWithoutId.getNotified());
        task.setUserid(user_id);
        task.setImportance(taskWithoutId.getImportance());

        try {
            taskdao.addTask(task);
            JsonObject json_response = new JsonObject();
            json_response.addProperty("id", task.getId());
            response.getWriter().write(gson.toJson(json_response));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "增加任务时候发生错误");
            response.getWriter().write(gson.toJson(error));
        }
    }
}
