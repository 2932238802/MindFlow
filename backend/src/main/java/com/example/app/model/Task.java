package com.example.app.model;

import com.example.app.util.GetId;

public class Task {

    private String name;
    private boolean isdelete;
    private boolean notified;
    private String time;
    private String id;
    private int user_id;
    private String importance;

    // 默认构造
    public Task(){}

    // 参数构造
    public Task(
        String name_out,
        String time_out,
        boolean isdelete_out,
        boolean notified_out,
        String id_out,
        String importance,
        int user_id
    ){
        this.name = name_out;
        this.time = time_out;
        this.notified = notified_out;
        this.isdelete = isdelete_out;
        this.id = id_out;
        this.importance = importance;
        this.user_id = user_id;
    }

    // 获取参数
    public String getName() {return this.name;}
    public String getTime(){return this.time;}
    public boolean getNotified() {return this.notified;}
    public boolean getIsdelete() {return this.isdelete;}
    public String getId() {return this.id;}
    public String getImportance() {return this.importance;}
    public int getUserId() {return this.user_id;}


    // 设置参数
    public void setName(String name_out) {this.name = name_out;}
    public void setTime(String time_out){this.time = time_out;}
    public void setNotified(Boolean is) {this.notified = is;}
    public void setIsdelete(Boolean is) {this.isdelete = is;}
    public void setId(String id) {this.id = id;}
    public void setImportance(String importance) {this.importance = importance;}
    public void setUserid(int user_id) {this.user_id = user_id;}

    public void getRandonId()
    {
        this.id = GetId.getUuid();
    }
}

