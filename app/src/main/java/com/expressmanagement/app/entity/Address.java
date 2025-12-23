package com.expressmanagement.app.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;

@Entity(tableName = "address",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "uid",
                childColumns = "user_id"))
public class Address {

    @PrimaryKey(autoGenerate = true)
    private int aid;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "receiver_name")
    private String receiverName;

    @ColumnInfo(name = "receiver_phone")
    private String receiverPhone;

    @ColumnInfo(name = "province")
    private String province;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "district")
    private String district;

    @ColumnInfo(name = "detail_address")
    private String detailAddress;

    @ColumnInfo(name = "is_default")
    private boolean isDefault;

    /**
     * 地址标签: 家/公司/学校
     */
    @ColumnInfo(name = "tag")
    private String tag;

    @ColumnInfo(name = "create_time")
    private long createTime;

    // 构造函数
    public Address() {
        this.createTime = System.currentTimeMillis();
        this.isDefault = false;
    }

    // Getters and Setters
    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    // 工具方法
    public String getFullAddress() {
        return province + city + district + detailAddress;
    }

    public String getShortAddress() {
        return city + district;
    }
}