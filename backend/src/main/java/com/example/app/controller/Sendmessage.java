package com.example.app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import com.example.app.dao.Dao;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException; // 引入 JsonSyntaxException

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/sendmessage")
public class Sendmessage extends HttpServlet {

    private static final Gson gson = new Gson();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("------------ API /api/sendmessage received ------------");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Dao dao = new Dao();
        StringBuilder json_payload_builder = new StringBuilder();
        String json_pay_string = null;

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                json_payload_builder.append(line);
            }
            json_pay_string = json_payload_builder.toString();

            System.out.println("Received JSON payload string: " + json_pay_string);

            if (json_pay_string == null || json_pay_string.trim().isEmpty()) {
                System.err.println("Error: Request payload is empty or null.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("error", "请求体为空!");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            JsonObject requestjson = gson.fromJson(json_pay_string, JsonObject.class);

            if (!requestjson.has("id") || requestjson.get("id").isJsonNull()) {
                System.err.println("Error: Missing 'id' field or 'id' is null in JSON payload.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("error", "请求中缺少 'id' 字段或 'id' 为空!");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            if (!requestjson.has("message") || requestjson.get("message").isJsonNull()) {
                System.err.println("Error: Missing 'message' field or 'message' is null in JSON payload.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("error", "请求中缺少 'message' 字段或 'message' 为空!");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            // userid 用户名字
            // message 发送信息
            int userid;
            String message;

            try {
                userid = requestjson.get("id").getAsInt();
            } catch (NumberFormatException | IllegalStateException e) {
                System.err.println("Error: 'id' field is not a valid integer in JSON payload.");
                e.printStackTrace(); // 打印异常堆栈
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("error", "'id' 字段必须是一个有效的整数!");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            try {
                message = requestjson.get("message").getAsString();
            } 
            // IllegalStateException 运行时候的错误
            catch (IllegalStateException e) {
                System.err.println("Error: 'message' field is not a valid string in JSON payload");
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("error", "'message' 字段必须是一个有效的字符串!");
                response.getWriter().write(gson.toJson(error));
                return;
            }

            System.out.println("Attempting to send message User ID: " + userid + ", Message: " + message);
            dao.sendMessage(userid, message);

            System.out.println("Message sent successfully for user_id: " + userid);
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonObject ok = new JsonObject();
            ok.addProperty("message", "信息发送成功");
            response.getWriter().write(gson.toJson(ok));
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format Received payload: " + json_pay_string);
            e.printStackTrace(); // 打印完整的异常堆栈
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("error", "请求的JSON格式无效!");
            response.getWriter().write(gson.toJson(error));
        }

        catch (SQLException e) {
            System.err.println("Failed to send message to database.");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "邮件发送失败 (数据库操作错误)");
            response.getWriter().write(gson.toJson(error));
        }

        catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "服务器内部发生未知错误");
            response.getWriter().write(gson.toJson(error));
        } finally {
            System.out.println("------------ API /api/sendmessage (POST) finished ------------"); // 出口日志
        }
    }
}
