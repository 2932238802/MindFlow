package com.example.app.controller;

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

@WebServlet("/api/cleartasks")
public class Cleartask extends HttpServlet {

  public void init() throws ServletException {
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    Gson gson = new Gson();
    Dao dao = new Dao();

    // 获取id 
    HttpSession session = request.getSession();
    if (session == null || session.getAttribute("user_id") == null) {
      // 说明还没认证
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("{\"message\":\"please auth first!\"}");
      return;
    }

    int user_id = (int) session.getAttribute("user_id");

    try {
      // 执行删除操作
      dao.clearTask(user_id);
      response.setStatus(HttpServletResponse.SC_OK);

      JsonObject result = new JsonObject();
      result.addProperty("message", "已清理所有标记为已删除的任务");

      // todo : √
      response.getWriter().write(gson.toJson(result));

    } catch (SQLException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      JsonObject error = new JsonObject();

      // todo : √
      error.addProperty("error", "清理已删除任务失败，请稍后重试");
      response.getWriter().write(gson.toJson(error));
    }
  }

  @Override
  public void destroy() {
    super.destroy();
  }
}