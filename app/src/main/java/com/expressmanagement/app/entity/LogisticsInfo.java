package com.expressmanagement.app.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;


@Entity(tableName = "logistics_info")
public class LogisticsInfo {

    @PrimaryKey(autoGenerate = true)
    private int lid;

    @ColumnInfo(name = "package_id")
    private int packageId;

    @ColumnInfo(name = "info")
    private String info;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public LogisticsInfo() {
        this.timestamp = System.currentTimeMillis();
    }

    public LogisticsInfo(int packageId, String info) {
        this.packageId = packageId;
        this.info = info;
        this.timestamp = System.currentTimeMillis();
    }

    public int getLid() { return lid; }
    public void setLid(int lid) { this.lid = lid; }
    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }
    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}