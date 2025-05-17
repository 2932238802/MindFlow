package com.example.app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import com.example.app.dao.Dao;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/sendmessage")
public class Sendmessage extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置回应格式
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 设置读取json文件的工具
        Gson gson = new Gson();

        // 设置数据库的接口
        Dao dao = new Dao();

        // 读取json文件的接口
        BufferedReader reader = null;

        // 存储String的容器 StringBuilder构建字符串的类
        StringBuilder jsonPayloadBuilder = new StringBuilder();

        // 读取到json文件里面
        try {
            reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonPayloadBuilder.append(line);
            }

            String jsonPayString = jsonPayloadBuilder.toString();

            if (jsonPayString.isEmpty()) {
                // 请求的问题
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                // 发送错误数据
                JsonObject error = new JsonObject();
                error.addProperty("error", "发送的内容为空!");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            // 再转成 json结构（从json字符串）
            JsonObject requestJson = gson.fromJson(jsonPayString, JsonObject.class);

            // 转成json结构体之后 要进行读取
            if (!requestJson.has("id")) {
                // 请求的问题
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                // 发送错误数据
                JsonObject error = new JsonObject();

                // todo:
                error.addProperty("error", "不存在发送的userid !");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            // 转成json结构体之后 要进行读取
            if (!requestJson.has("message")) {
                // 请求的问题
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                // 发送错误数据
                JsonObject error = new JsonObject();

                // todo:
                error.addProperty("error", "不存在发送的信息message!");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            // 说明存在userid
            // 编辑信息
            // 确认状态 201
            // 发送 message:信息发送成功 todo: √
            int userid = requestJson.get("id").getAsInt();
            String message = requestJson.get("message").getAsString();
            dao.sendMessage(userid, message);
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonObject ok = new JsonObject();
            ok.addProperty("message", "信息发送成功");
            response.getWriter().write(gson.toJson(ok));
        }

        catch (SQLException e) {
            // 确认状态 500
            // 编辑信息
            // 发送 error:邮件发送失败 todo: √
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "邮件发送失败");
            response.getWriter().write(gson.toJson(error));
        }
    }
}
