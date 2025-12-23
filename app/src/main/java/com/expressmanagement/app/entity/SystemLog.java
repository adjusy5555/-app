package com.expressmanagement.app.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "system_log")
public class SystemLog {

    @PrimaryKey(autoGenerate = true)
    private int lid;

    @ColumnInfo(name = "operator_id")
    private int operatorId;

    /**
     * 操作者角色: 0-用户, 1-快递员, 2-管理员
     */
    @ColumnInfo(name = "operator_role")
    private int operatorRole;

    @ColumnInfo(name = "action_type")
    private String actionType;

    /**
     * 操作对象类型: User/Package/Address
     */
    @ColumnInfo(name = "target_type")
    private String targetType;

    @ColumnInfo(name = "target_id")
    private Integer targetId;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // 构造函数
    public SystemLog() {
        this.timestamp = System.currentTimeMillis();
    }

    public SystemLog(int operatorId, int operatorRole, String actionType,
                     String targetType, Integer targetId, String description) {
        this.operatorId = operatorId;
        this.operatorRole = operatorRole;
        this.actionType = actionType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public int getOperatorRole() {
        return operatorRole;
    }

    public void setOperatorRole(int operatorRole) {
        this.operatorRole = operatorRole;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}