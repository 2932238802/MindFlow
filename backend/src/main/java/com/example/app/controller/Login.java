package main.java.com.example.app.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.app.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 监听登录信息
 * 它监听 /api/login 路径 (相对于应用的上下文根)
 */
@WebServlet("/api/login")
public class Login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        //
        String path = request.getRequestURI();

        if(path.endsWith("/login"))
        {
            HandleLogin(request,response);
        }

    }

    /**
     * 私有成员函数 处理登录请求
     * 处理登录请求 然后和数据库验证一下
     * @param request  这个是请求
     * @param response 这个是响应
     */
    private void HandleLogin(HttpServletRequest request, HttpServletResponse response) {
        // 获取姓名 //
        String user_name = request.getParameter("user_name");

        // 获取密码 //
        String password = request.getParameter("password");

        // 是不是记住 //
        boolean remember = Boolean.parseBoolean(request.getParameter("remember"));

        // 根据数据库 处理一下 信息是不是正确 //
        try {
            // getConnection 返回一个 Connection //
            Connection connect = db.getConnection();

            // 创建一个查询的语句 //
            String sql_query = "select * from users where user_name = ? and password = ?";

            // 使用 prepareStatement() 预处理 SQL 语句
            PreparedStatement prep = connect.prepareStatement(sql_query);

            // 为 SQL 查询中的占位符绑定参数 //
            // 第一个问号绑定用户名参数 (1 表示第一个问号) //
            // 第二个问号绑定用户密码参数 (2 表示第二个问号) //
            prep.setString(1, user_name);
            prep.setString(2, password);

            // 执行查询操作
            ResultSet result = prep.executeQuery();

            if (result.next()) {
                // 登录成功返ok //
                response.setStatus(HttpServletResponse.SC_OK);
                System.out.println("Login successful! User ID: " + result.getInt("user_id"));
            } else {
                // 不然认证失败 //
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                System.out.println("Login failed! Incorrect username or password.");
            }

            db.close(connect);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
