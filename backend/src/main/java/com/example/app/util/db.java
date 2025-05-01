package main.java.com.example.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class db {

    // MindFlow
    private static final String URL = "jdbc:mysql://localhost:3306/MindFlow";

    // 这个 root 就是启动mysql的用户名 （mysql -u root -p ） 这个root就是用户名字
    private static final String USER = "root";

    // 密码 免费登录
    private static final String PASSWORD = "";

    /**
     *
     * @return
     * @throws SQLException 抛出错误
     */
    public static Connection getConnection() throws SQLException {
        // 增加驱动
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }

        catch (ClassNotFoundException | SQLException e) {
            throw new SQLException("Database connection error", e);
        }
    }

    public static void close(Connection connect) {
        if (connect != null) {
            try {
                connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}