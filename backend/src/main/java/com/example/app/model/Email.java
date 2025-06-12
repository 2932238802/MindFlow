package com.example.app.model;

import java.sql.Timestamp;

public class Email {
    /**
     * 消息类
     * id 就是唯一标识符
     * user_id 就是用户的标识名字
     * message 消息
     * sent_at 发送的时间
     */

    private int id;
    private int user_id;
    private String message;
    private Timestamp sent_at;

    public Email() {
    }

    public Email(int id, int userId, String message, Timestamp sentAt) {
        this.id = id;
        this.user_id = userId;
        this.message = message;
        this.sent_at = sentAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int userId) {
        this.user_id = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getSentAt() {
        return sent_at;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sent_at = sentAt;
    }
}
