package com.example.app.model;

public class User {

    int id;
    String email;
    String user_name;

    public void setId(int id_out) {
        this.id = id_out;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public int getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUserName() {
        return this.user_name;
    }
}
