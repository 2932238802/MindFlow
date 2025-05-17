package com.example.app.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.example.app.dao.Dao;
import com.example.app.model.Task;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import com.google.gson.Gson;

/**
 * 听取如果 倾听接口获取信息 返回数据
 */
@WebServlet("/api/loadtask")
public class LoadTasks extends HttpServlet {

  @Override
  public void init() throws ServletException
  {
  }

  @Override
  public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
    // 设置基础格式
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // 获取 user_id
    HttpSession session = request.getSession();
    if (session == null || session.getAttribute("user_id") == null) {
      // 说明还没认证
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

      // todo frontend :
      response.getWriter().write("{\"message\":\"please auth first!\"}");
      return;
    }
    int user_id = (int) session.getAttribute("user_id");


    Dao td = new Dao();

    try{
      // 1. 获取数据
      List<Task> tasks = td.getAlltask(user_id);

      // 2. 转换成 json格式
      Gson gson = new Gson();
      String json_response = gson.toJson(tasks);

      // 3. 发送响应
      response.getWriter().write(json_response);
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

      // todo : 
      response.getWriter().write("{\"error:\":\"数据库错误!\"}");
    }
  }
  
   @Override
  public void destroy() {
    super.destroy();
  }
}