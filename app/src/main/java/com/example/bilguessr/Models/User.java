package com.example.bilguessr.Models;

public class User {
    private int hotPursuitRecord;
    private int timeRushRecord;
    private String email;
    private String name;
    private int isAdmin;

    private String userPhotoUrl;

    public int getHotPursuitRecord() {
        return hotPursuitRecord;
    }

    public int getTimeRushRecord() {
        return timeRushRecord;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getIsAdmin() {
        return isAdmin;
    }
    public String getUserPhotoUrl()
    {
        return userPhotoUrl;
    }
    public User()
    {}
    public User(int hotPursuitRecord, int timeRushRecord, String email, String name, int isAdmin, String userPhotoUrl) {
        this.hotPursuitRecord = hotPursuitRecord;
        this.timeRushRecord = timeRushRecord;
        this.email = email;
        this.name = name;
        this.isAdmin = isAdmin;
        this.userPhotoUrl = userPhotoUrl;
    }
}
