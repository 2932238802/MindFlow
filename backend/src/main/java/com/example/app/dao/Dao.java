package com.example.app.dao;

import com.example.app.model.Email;
import com.example.app.model.Task;
import com.example.app.model.User;
import com.example.app.util.DB;
import com.example.app.util.PasswordEncrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Dao {

    // 登录信息查询
    public int checkUser(String user_name, String password) throws SQLException
    {
        try (
                // 获取数据库连接 //
                Connection connect = DB.getConnection();

                // 创建一个查询的语句 //
                PreparedStatement prep = connect.prepareStatement(
                        "SELECT * FROM users WHERE user_name = ?")
        ) {
            // 为 SQL 查询中的占位符绑定参数
            // 第一个问号绑定用户名参数 (1 表示第一个问号)
            // 第二个问号绑定用户密码参数 (2 表示第二个问号)
            prep.setString(1, user_name);

            // 执行查询操作 并使用 try-with-resources 确保 ResultSet 自动关闭 //
            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    // 获得mysql的数据 //
                    String hashedPassword = result.getString("password");

                    int user_id = result.getInt("user_id");

                    if (PasswordEncrypt.verify(password, hashedPassword)) {
                        return user_id;
                    }
                    else {
                        return -1;
                    }
                }
                else {
                    return -1;
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // 获取所有任务
    public boolean addUser(String user_name, String encry_pass_word, String email) throws SQLException {
        String prep_check = "SELECT COUNT(*) AS count FROM users WHERE user_name = ? OR email = ?";

        String prep_insert = "INSERT INTO users (user_name, password, email) VALUES (?,?,?)";

        // executeQuery() SELECT 查询 ResultSet
        // executeUpdate() INSERT/UPDATE/DELETE 等修改语句 int（受影响行数）
        try (Connection connection = DB.getConnection();
                PreparedStatement query_check_format = connection.prepareStatement(prep_check);
                PreparedStatement query_insert_format = connection.prepareStatement(prep_insert);

        ) {
            query_check_format.setString(1, user_name);
            query_check_format.setString(2, email);

            // 返回查询结果
            ResultSet result = query_check_format.executeQuery();

            if (result.next() && result.getInt("count") > 0) {
                return false;
            }

            // 没有返回 那么就是正常运行
            query_insert_format.setString(1, user_name);
            query_insert_format.setString(2, encry_pass_word);
            query_insert_format.setString(3, email);

            int flag = query_insert_format.executeUpdate();
            if (flag > 0) {
                return true;
            }
        }
        return false;
    }

    public List<Task> getAlltask(int user_id_out) throws SQLException {

        // 创建一个链表
        List<Task> tasks = new ArrayList<>();

        // 创建访问语句
        String query = "SELECT id,name,isdeleted,importance,time,notified FROM tasks WHERE user_id = ?;";

        try (Connection connection = DB.getConnection();
                PreparedStatement query_format = connection.prepareStatement(query)) {

            query_format.setInt(1, user_id_out);
            ResultSet result = query_format.executeQuery();

            // 向前滚动
            while (result.next()) {
                // 初始化
                Task task_tmp = new Task();
                task_tmp.setName(result.getString("name"));
                task_tmp.setTime(result.getString("time"));
                task_tmp.setNotified(result.getBoolean("notified"));
                task_tmp.setIsdelete(result.getBoolean("isdeleted"));
                task_tmp.setId(result.getString("id"));
                task_tmp.setImportance(result.getString("importance"));
                // 设置
                tasks.add(task_tmp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> userdata = new ArrayList<User>();
        String query = "SELECT user_id,user_name,email FROM users";

        try (Connection connection = DB.getConnection();
                PreparedStatement query_format = connection.prepareStatement(query)) {
            ResultSet result = query_format.executeQuery();
            while (result.next()) {
                User user = new User();
                user.setId(result.getInt("user_id"));
                user.setEmail(result.getString("email"));
                user.setUserName(result.getString("user_name"));

                userdata.add(user);
            }
        }
        return userdata;

    }

    public void addTask(Task task_out) throws SQLException {
        String query = "INSERT INTO tasks (id,user_id,name,isdeleted,importance,time,notified) VALUES (?,?,?,?,?,?,?);";

        try (Connection connection = DB.getConnection();
                // 指示 prepareStatement 方法在执行 SQL 语句后返回自动生成的键
                PreparedStatement query_format = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            query_format.setString(1, task_out.getId());
            query_format.setInt(2, task_out.getUserId());
            query_format.setString(3, task_out.getName());
            query_format.setBoolean(4, task_out.getIsdelete());
            query_format.setString(5, task_out.getImportance());
            query_format.setString(6, task_out.getTime());
            query_format.setBoolean(7, task_out.getNotified());
            query_format.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改任务状态
    public Boolean alterTaskStata(String id_out, int user_id_out) throws SQLException {

        String query = "UPDATE tasks SET isdeleted = NOT isdeleted WHERE id = ? AND user_id = ?;";
        String check = "SELECT isdeleted FROM tasks WHERE id = ? AND user_id = ?;";
        Boolean for_return = false;

        try (Connection connection = DB.getConnection();
                PreparedStatement query_format = connection.prepareStatement(query);
                PreparedStatement check_format = connection.prepareStatement(check)) {
            query_format.setString(1, id_out);
            query_format.setInt(2, user_id_out);
            query_format.executeUpdate(); // executeUpdate 返回影响的行数
                                          // executeQuery 返回select

            check_format.setString(1, id_out);
            check_format.setInt(2, user_id_out);
            ResultSet returnset = check_format.executeQuery();
            for_return = returnset.getBoolean("isdeleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return for_return;
    }

    /**
     * clearTask 清理用户点击删除的任务
     * 1. user_id 用户id
     * 2. SQLException 异常
     */
    // TODO: √
    public void clearTask(int user_id) throws SQLException {
        String query = "DELETE FROM tasks WHERE isdeleted = TRUE AND user_id = ?;";

        try (Connection connection = DB.getConnection();
                PreparedStatement query_format = connection.prepareStatement(query)) {
            query_format.setInt(1, user_id);
            query_format.executeUpdate();
        }
    }

    // 删除
    public void deleteUser(int user_id) throws SQLException {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (
                Connection connection = DB.getConnection();
                PreparedStatement query_format = connection.prepareStatement(query)) {
            query_format.setInt(1, user_id);
            query_format.executeUpdate();
        }
    }

    public void sendMessage(int user_id, String message) throws SQLException {
        String query = "INSERT into email (user_id,message) values(?,?)";

        try (
                Connection connection = DB.getConnection();
                PreparedStatement query_format = connection.prepareStatement(query)) {
            query_format.setInt(1, user_id);
            query_format.setString(2, message);
            query_format.executeUpdate();
        }
    }

    public List<Email> getAllMessage(int userId) throws SQLException {
        List<Email> emails = new ArrayList<>();
        String query = "SELECT id, user_id, message, sent_at FROM email WHERE user_id = ?";

        try (Connection connection = DB.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    Email email = new Email();
                    email.setId(result.getInt("id"));
                    email.setUserId(result.getInt("user_id"));
                    email.setMessage(result.getString("message"));
                    email.setSentAt(result.getTimestamp("sent_at"));
                    emails.add(email);
                }
            }
        }
        return emails;
    }

}
