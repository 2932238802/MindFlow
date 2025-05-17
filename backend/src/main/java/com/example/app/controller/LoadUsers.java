package com.example.app.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.example.app.dao.Dao;
import com.example.app.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import com.google.gson.Gson;

@WebServlet("/api/loaduser")
public class LoadUsers extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request , HttpServletResponse response) throws ServletException,IOException{
        // 1. 设置回应格式 json
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 2. 返回对应的数据
        Dao td = new Dao();


        try{
            // 1. 获取数据
            List<User> users = td.getAllUsers();

            // 2. 转换成 json格式
            Gson gson = new Gson();
            String json_response = gson.toJson(users);

            // 3. 发送响应
            response.getWriter().write(json_response);
        }
        catch(SQLException e)
        {
            e.printStackTrace();

            // todo: √
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            // todo : √
            response.getWriter().write("{\"error:\":\"读取用户数据的时候 数据库错误!\"}");
        }
    }
}
