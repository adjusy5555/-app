package com.expressmanagement.app.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "user")
public
class User {

    /**
     * 账号状态: 0-正常, 1-已注销/删除
     */
    @ColumnInfo(name = "status", defaultValue = "0")
    private int status;

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "nickname")
    private String nickname;

    /**
     * 角色: 0-普通用户, 1-快递员, 2-管理员
     */
    @ColumnInfo(name = "role")
    private int role;

    @ColumnInfo(name = "create_time")
    private long createTime;

    // 构造函数
    public User() {
    }

    public User(String username, String password, String phone, int role) {
        this.status = 0;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.createTime = System.currentTimeMillis();
    }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }

    public boolean isValid() { return status == 0; }

    // Getters and Setters
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    // 角色判断方法
    public boolean isUser() {
        return role == 0;
    }

    public boolean isCourier() {
        return role == 1;
    }

    public boolean isAdmin() {
        return role == 2;
    }

    public String getRoleName() {
        switch (role) {
            case 0: return "普通用户";
            case 1: return "快递员";
            case 2: return "管理员";
            default: return "未知";
        }
    }
}