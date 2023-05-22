package com.example.foodapp.Model;

public class UserDetailNote {
    private String username;
    private String uid;
    private String phone_no;

    public UserDetailNote() {
    }

    public UserDetailNote(String username, String uid, String phone_no) {
        this.username = username;
        this.uid = uid;
        this.phone_no = phone_no;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }
}
