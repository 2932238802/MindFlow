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
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/altertask")
public class ToggleTask extends HttpServlet {

    @Override
    public void init() throws ServletException {
    }

    @Override
    public void doPut(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();

        // 获取 json 和 数据库操纵
        Dao td = new Dao();
        Gson gson = new Gson();

        // 获取user id
        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("user_id") == null) {
            // 说明还没认证
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // TODO: 前端
            response.getWriter().write("{\"message\":\"please auth first!\"}");
            return;
        }
        int user_id = (int) session.getAttribute("user_id");

        JsonObject jsonObject = gson.fromJson(requestBody, JsonObject.class);
        String id = jsonObject.get("id").getAsString();

        try {
            // 获取目前数据库中的值
            Boolean theid_isdelete = td.alterTaskStata(id,user_id);

            JsonObject jsonresponse = new JsonObject();
            jsonresponse.addProperty("isdelete_from_backend", theid_isdelete);
            jsonresponse.addProperty("id", id);
            response.getWriter().write(gson.toJson(jsonresponse));
        } catch (SQLException e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorresponse = new JsonObject();
            errorresponse.addProperty("error", "改变任务的时候发生问题");
            response.getWriter().write(gson.toJson(errorresponse));
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}