package com.expressmanagement.app.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;

@Entity(tableName = "package",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "uid",
                        childColumns = "sender_id",
                        onDelete = ForeignKey.NO_ACTION),
                @ForeignKey(entity = Address.class,
                        parentColumns = "aid",
                        childColumns = "address_id",
                        onDelete = ForeignKey.NO_ACTION)
        })
public class Package {

    @PrimaryKey(autoGenerate = true)
    private int pid;

    @ColumnInfo(name = "tracking_no")
    private String trackingNo;

    @ColumnInfo(name = "sender_id")
    private int senderId;

    @ColumnInfo(name = "receiver_name")
    private String receiverName;

    @ColumnInfo(name = "receiver_phone")
    private String receiverPhone;

    @ColumnInfo(name = "address_id")
    private int addressId;

    @ColumnInfo(name = "item_type")
    private String itemType;

    @ColumnInfo(name = "weight")
    private float weight;

    @ColumnInfo(name = "price")
    private float price;

    /**
     * 状态: 0-待揽件, 1-已揽件, 2-运输中, 3-派送中, 4-已签收, 5-已取消
     */
    @ColumnInfo(name = "status")
    private int status;

    @ColumnInfo(name = "courier_id")
    private Integer courierId;

    @ColumnInfo(name = "create_time")
    private long createTime;

    @ColumnInfo(name = "update_time")
    private long updateTime;

    @ColumnInfo(name = "remark")
    private String remark;

    // 构造函数
    public Package() {
        this.status = 0;
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getCourierId() {
        return courierId;
    }

    public void setCourierId(Integer courierId) {
        this.courierId = courierId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    // 工具方法
    public String getStatusName() {
        switch (status) {
            case 0: return "待揽件";
            case 1: return "已揽件";
            case 2: return "运输中";
            case 3: return "派送中";
            case 4: return "已签收";
            case 5: return "已取消";
            default: return "未知状态";
        }
    }

    public boolean canCancel() {
        return status == 0;
    }

    public boolean canConfirmReceipt() {
        return status == 3;
    }
}