package com.example.app.model;

import com.example.app.util.GetId;

public class Task {

    private String name;
    boolean isdelete;
    boolean notified;
    private String time;
    String id;

    // 默认构造
    public Task(){}

    // 参数构造
    public Task(
        String name_out,
        String time_out,
        boolean isdelete_out,
        boolean notified_out,
        String id_out
    ){
        this.name = name_out;
        this.time = time_out;
        this.notified = notified_out;
        this.isdelete = isdelete_out;
        this.id = id_out;
    }

    // 获取参数
    public String getName() {return this.name;}
    public String getTime(){return this.time;}
    public boolean getNotified() {return this.notified;}
    public boolean getIsdelete() {return this.isdelete;}
    public String getId() {return this.id;}


    // 设置参数
    public void setName(String name_out) {this.name = name_out;}
    public void setTime(String time_out){this.time = time_out;}
    public void setNotified(Boolean is) {this.notified = is;}
    public void setIsdelete(Boolean is) {this.isdelete = is;}
    public void setId(String id_out) {this.id = id_out;}

    public void getRandonId()
    {
        this.id = GetId.getUuid();
    }
}

