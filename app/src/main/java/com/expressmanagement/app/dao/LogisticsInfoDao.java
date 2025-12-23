package com.expressmanagement.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.expressmanagement.app.entity.LogisticsInfo;

import java.util.List;

@Dao
public interface LogisticsInfoDao {

    @Insert
    long insert(LogisticsInfo logisticsInfo);

    @Update
    void update(LogisticsInfo logisticsInfo);

    @Delete
    void delete(LogisticsInfo logisticsInfo);

    @Query("SELECT * FROM logistics_info WHERE package_id = :packageId ORDER BY timestamp DESC")
    List<LogisticsInfo> getLogisticsByPackageId(int packageId);

    @Query("SELECT * FROM logistics_info WHERE package_id = :packageId ORDER BY timestamp ASC LIMIT 1")
    LogisticsInfo getFirstLogistics(int packageId);

    @Query("SELECT * FROM logistics_info WHERE package_id = :packageId ORDER BY timestamp DESC LIMIT 1")
    LogisticsInfo getLatestLogistics(int packageId);

    @Query("DELETE FROM logistics_info WHERE package_id = :packageId")
    void deleteByPackageId(int packageId);
}