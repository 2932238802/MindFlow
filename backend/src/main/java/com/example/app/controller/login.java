
package com.example.api; // 替换成你项目的包名

import jakarta.servlet.ServletException; // 使用 Jakarta EE 9+ API
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// 如果你的 Tomcat 较旧，可能需要使用 javax.servlet.*

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 一个简单的 Servlet，用于响应状态检查请求
 * 它监听 /api/status 路径 (相对于应用的上下文根)
 */
@WebServlet("/api/register")
public class StatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

    }
}
