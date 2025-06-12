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

@WebServlet("/api/addtask")
public class AddTask extends HttpServlet {
    /**
     * 1. AddTask 添加任务的类
     * 2. doPost 处理post请求
     */

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
         * 1. 处理post请求
         */

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        // 获取json 转换器
        // 获取后端与数据库的接口
        // StringBuilder 字符串构建器
        Gson gson = new Gson();
        Dao taskdao = new Dao();
        StringBuilder sb = new StringBuilder();

        // 读取请求体内容
        // try自动释放reader
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        // request_body 请求内容
        // task_withoutId
        String request_body = sb.toString();
        Task task_withoutId = gson.fromJson(request_body, Task.class);
        String generated_id = GetId.getUuid();
        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("user_id") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\":\"please auth first!\"}");
            return;
        }

        int user_id = (int) session.getAttribute("user_id");

        Task task = new Task();
        task.setId(generated_id);
        task.setName(task_withoutId.getName());
        task.setIsdelete(task_withoutId.getIsdelete());
        task.setTime(task_withoutId.getTime());
        task.setNotified(task_withoutId.getNotified());
        task.setUserid(user_id);
        task.setImportance(task_withoutId.getImportance());

        try {
            taskdao.addTask(task);
            JsonObject json_response = new JsonObject();
            // addProperty这个是JsonObject用来增加属性的

            json_response.addProperty("id", task.getId());
            response.getWriter().write(gson.toJson(json_response));
        } 
        catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();

            // 错误输出
            error.addProperty("error", "增加任务时候发生错误");
            response.getWriter().write(gson.toJson(error));
        }
    }
}
