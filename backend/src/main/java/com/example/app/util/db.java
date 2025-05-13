

package com.example.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class db {

    // 数据库 URL，连接到 MindFlow 数据库
    private static final String URL = "jdbc:mysql://localhost:3306/MindFlow?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // 使用新创建的用户 MindFlowApp
    private static final String USER = "MindFlowApp";

    // 使用 MindFlowApp 的密码
    private static final String PASSWORD = "!Lsj20041021";

    /**
     *  获取数据库连接
     *
     * @return Connection 返回数据库连接
     * @throws SQLException 抛出错误
     */
    public static Connection getConnection() throws SQLException {
        // 加载 JDBC 驱动
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new SQLException("Database connection error", e);
        }
    }

    /**
     * 关闭数据库连接
     *
     * @param connect Connection 对象
     */
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
