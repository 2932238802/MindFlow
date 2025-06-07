package com.example.app.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.io.BufferedReader;
import com.example.app.dao.Dao;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/deleteuser")
public class DeleteUser extends HttpServlet {

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
                error.addProperty("error", "请求内容是空的!");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            // 再转成 json结构（从json字符串）
            JsonObject requestJson = gson.fromJson(jsonPayString, JsonObject.class);

            // 转成json结构体之后 要进行读取
            if (!requestJson.has("userid")) {
                // 请求的问题
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                // 发送错误数据
                JsonObject error = new JsonObject();
                error.addProperty("error", "不存在请求的userid 无法进行删除!");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            // 说明存在userid
            int userid = requestJson.get("userid").getAsInt();
            dao.deleteUser(userid);

            response.setStatus(HttpServletResponse.SC_OK);
            JsonObject ok = new JsonObject();
            ok.addProperty("message", "用户删除成功");
            response.getWriter().write(gson.toJson(ok));
        }

        catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "删除用户失败，请稍后重试");
            response.getWriter().write(gson.toJson(error));
        }
    }
}