package com.expressmanagement.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.expressmanagement.app.entity.Package;

import java.util.List;

@Dao
public interface PackageDao {

    @Insert
    long insert(Package pkg);

    @Update
    void update(Package pkg);

    @Delete
    void delete(Package pkg);

    @Query("SELECT * FROM package WHERE pid = :pid")
    Package getPackageById(int pid);

    @Query("SELECT * FROM package WHERE tracking_no = :trackingNo")
    Package getPackageByTrackingNo(String trackingNo);

    // 用户相关查询
    @Query("SELECT * FROM package WHERE sender_id = :userId ORDER BY create_time DESC")
    List<Package> getSentPackages(int userId);

    @Query("SELECT * FROM package WHERE receiver_phone = :phone ORDER BY create_time DESC")
    List<Package> getReceivedPackages(String phone);

    // 快递员相关查询
    @Query("SELECT * FROM package WHERE status = 0 ORDER BY create_time DESC")
    List<Package> getAvailablePackages();

    @Query("SELECT * FROM package WHERE courier_id = :courierId AND status != 4 AND status != 5 ORDER BY update_time DESC")
    List<Package> getCourierActivePackages(int courierId);

    @Query("SELECT * FROM package WHERE courier_id = :courierId AND status = 4 ORDER BY update_time DESC")
    List<Package> getCourierCompletedPackages(int courierId);

    // 管理员相关查询
    @Query("SELECT * FROM package ORDER BY create_time DESC")
    List<Package> getAllPackages();

    @Query("SELECT * FROM package WHERE status = :status ORDER BY create_time DESC")
    List<Package> getPackagesByStatus(int status);

    // 自动签收相关查询
    @Query("SELECT * FROM package WHERE status = :status AND update_time < :expireTime")
    List<Package> getExpiredPackages(int status, long expireTime);

    // 统计查询
    @Query("SELECT COUNT(*) FROM package")
    int getTotalCount();

    @Query("SELECT COUNT(*) FROM package WHERE create_time >= :startTime")
    int getTodayCount(long startTime);

    @Query("SELECT COUNT(*) FROM package WHERE status = 4")
    int getCompletedCount();

    @Query("SELECT COUNT(*) FROM package WHERE courier_id = :courierId AND status = 4")
    int getCourierCompletedCount(int courierId);

    @Query("SELECT COUNT(*) FROM package WHERE courier_id = :courierId AND status = 4 AND update_time >= :startTime")
    int getCourierCompletedCountByTime(int courierId, long startTime);

    @Query("SELECT * FROM package WHERE courier_id = :courierId AND status = :status ORDER BY update_time DESC")
    List<Package> getCourierPackagesByStatus(int courierId, int status);

    @Query("DELETE FROM package WHERE pid = :pid")
    void deleteById(int pid);
}