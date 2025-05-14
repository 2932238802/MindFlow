package com.example.app.dao;

import com.example.app.model.Task;
import com.example.app.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {

    // 获取所有任务
    public List<Task> getAlltask() throws SQLException{
        
        // 创建一个链表
        List<Task> tasks = new ArrayList<>();

        // 创建访问语句
        String query = "SELECT id,name,isdeleted,time,notified FROM tasks;";

        try(Connection connection = DB.getConnection();
            PreparedStatement query_format = connection.prepareStatement(query)){
            ResultSet result = query_format.executeQuery();

            // 向前滚动
            while(result.next())
            {
                // 初始化
                Task task_tmp = new Task();
                task_tmp.setName(result.getString("name"));
                task_tmp.setTime(result.getString("time"));
                task_tmp.setNotified(result.getBoolean("notified"));
                task_tmp.setIsdelete(result.getBoolean("isdeleted"));
                task_tmp.setId(result.getString("id"));

                // 设置
                tasks.add(task_tmp);
            }
        }
        return tasks;
    }

    public void addTask(Task task_out) throws SQLException
    {
        String query = "INSERT INTO tasks (id,name,isdeleted,time,notified) VALUES (?,?,?,?,?);";

        try(Connection connection = DB.getConnection();
            // 指示 prepareStatement 方法在执行 SQL 语句后返回自动生成的键  
            PreparedStatement query_format = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)){
            query_format.setString(1, task_out.getId());
            query_format.setString(2, task_out.getName());
            query_format.setBoolean(3, task_out.getIsdelete());
            query_format.setString(4, task_out.getTime());
            query_format.setBoolean(5, task_out.getNotified());
            query_format.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改任务状态
    public Boolean alterTaskStata(String id_out) throws SQLException
    {

        String query = "UPDATE tasks SET isdeleted = NOT isdeleted WHERE id = ? ;";
        String check = "SELECT isdeleted FROM tasks WHERE id = ?;";
        Boolean for_return = false;
        try(Connection connection = DB.getConnection();
            PreparedStatement query_format = connection.prepareStatement(query);
            PreparedStatement check_format = connection.prepareStatement(check)
            )
            {
                query_format.setString(1, id_out);
                query_format.executeUpdate();                   // executeUpdate 返回影响的行数
                                                                // executeQuery 返回select
        
                check_format.setString(1, id_out);
                ResultSet returnset = check_format.executeQuery();
                for_return = returnset.getBoolean("isdeleted");
            }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return for_return;
    }

    public void clearTask() throws SQLException
    {
        String query = "DELETE FROM tasks WHERE isdeleted = TRUE;";

        try(Connection connection = DB.getConnection();
            PreparedStatement query_format = connection.prepareStatement(query)){
                query_format.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
