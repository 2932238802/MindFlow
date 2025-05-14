package com.example.app.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.example.app.dao.TaskDao;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    TaskDao dao = new TaskDao();
    try {
      // 执行删除操作
      dao.clearTask();
      response.setStatus(HttpServletResponse.SC_OK);

      // 可选：返回操作结果
      JsonObject result = new JsonObject();
      result.addProperty("message", "已清理所有标记为已删除的任务");
      response.getWriter().write(gson.toJson(result));

    } catch (SQLException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      JsonObject error = new JsonObject();
      error.addProperty("error", "清理已删除任务失败，请稍后重试");
      response.getWriter().write(gson.toJson(error));
    }
  }

  @Override
  public void destroy() {
    super.destroy();
  }
}