package com.example.app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import com.example.app.dao.TaskDao;
import com.example.app.model.Task;
import com.example.app.util.GetId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.logging.Logger;

@WebServlet("/api/addtask")
public class AddTask extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AddTask.class.getName());
    @Override
    public void init() throws ServletException {
        LOGGER.info("********** AddTask Servlet Initialized **********");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        Gson gson = new Gson();
        TaskDao taskdao = new TaskDao();
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();

        LOGGER.info("AddTask doPost Invoked. Received request body: " + requestBody);

        Task taskWithoutId = gson.fromJson(requestBody, Task.class);
        String generatedId = GetId.getUuid();                                           // 通过 GetId 类生成唯一的 id

        // 创建新的 Task 对象，并设置手动生成的 id
        Task task = new Task();
        task.setId(generatedId);
        task.setName(taskWithoutId.getName());
        task.setIsdelete(taskWithoutId.getIsdelete());
        task.setTime(taskWithoutId.getTime());
        task.setNotified(taskWithoutId.getNotified());

        LOGGER.info("Attempting to add task to DAO. Task data: " + gson.toJson(task));
        try{
            taskdao.addTask(task);
            JsonObject json_response = new JsonObject();

            // todo:  √
            json_response.addProperty("id", task.getId());
            response.getWriter().write(gson.toJson(json_response));
        }
        catch(SQLException e)
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();

            // todo:  √
            error.addProperty("error", "增加任务时候发生错误");
            response.getWriter().write(gson.toJson(error));
        }
    }
}
