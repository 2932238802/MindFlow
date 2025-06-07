package com.example.app.dao;

import com.example.app.model.Email;
import com.example.app.model.Task;
import com.example.app.model.User;
import com.example.app.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Dao {

    // 获取所有任务
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
    // todo : √
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
