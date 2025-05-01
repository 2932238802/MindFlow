package main.java.com.example.app.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.java.com.example.app.util.db;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/api/register")
public class Register extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getRequestURI();
        if (path.endsWith("/register")) {
            HandleRegister(request, response);
        }
    }

    /**
     * 为用户注册处理逻辑
     *
     * @param request  这个是请求
     * @param response 这个是响应
     */
    private void HandleRegister(HttpServletRequest request, HttpServletResponse response) {
        // 获取姓名 //
        String user_name = request.getParameter("user_name");

        // 获取密码 //
        String password = request.getParameter("password");

        // 获取邮箱 //
        String email = request.getParameter("email");

        // 使用 try-with-resources 自动管理资源，保证所有数据库资源都正确关闭 //
        try (
                // 连接数据库 //
                Connection connection = db.getConnection();

                // 准备查询语句 以检查是否重复 //
                PreparedStatement prep = connection.prepareStatement(
                        "SELECT COUNT(*) AS count FROM users WHERE user_name = ? OR email = ?");

                // 准备插入新数据的语句 //
                PreparedStatement prep_insert = connection.prepareStatement(
                        "INSERT INTO users (user_name, password, email) VALUES (?,?,?)")
        ) {
            // 检查是否重复 //
            prep.setString(1, user_name);
            prep.setString(2, email);

            try (ResultSet result = prep.executeQuery()) {
                if (result.next() && result.getInt("count") > 0) {
                    // 如果重复 返回错误 //
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    return;
                }
            }

            // 插入新数据 //
            String encrypted_password = PasswordUtils.encrypt(password); // 示例加密，需自己实现加密工具类

            prep_insert.setString(1, user_name);
            prep_insert.setString(2, encrypted_password);
            prep_insert.setString(3, email);

            // executeUpdate() 是 PreparedStatement 对象的一个方法 //
            // 它执行的是与数据库更新有关的 SQL 语句，例如 INSERT、UPDATE 和 DELETE //
            // 并返回被影响的行数（即完成操作后数据库发生更改的记录数） //
            int rows_affected = prep_insert.executeUpdate();

            if (rows_affected > 0) {
                // 注册成功 //
                // 状态码 201 //
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                // 注册失败 //
                // 状态码 500 //
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
