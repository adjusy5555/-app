package com.expressmanagement.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expressmanagement.app.entity.SystemLog;

import java.util.List;

@Dao
public interface SystemLogDao {

    @Insert
    long insert(SystemLog systemLog);

    @Delete
    void delete(SystemLog systemLog);

    @Query("SELECT * FROM system_log ORDER BY timestamp DESC")
    List<SystemLog> getAllLogs();

    @Query("SELECT * FROM system_log WHERE operator_id = :operatorId ORDER BY timestamp DESC")
    List<SystemLog> getLogsByOperator(int operatorId);

    @Query("SELECT * FROM system_log WHERE action_type = :actionType ORDER BY timestamp DESC")
    List<SystemLog> getLogsByActionType(String actionType);

    @Query("SELECT * FROM system_log WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    List<SystemLog> getLogsByTimeRange(long startTime, long endTime);

    @Query("SELECT * FROM system_log WHERE operator_id = :operatorId AND action_type = :actionType AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    List<SystemLog> getLogsWithFilters(int operatorId, String actionType, long startTime, long endTime);

    @Query("DELETE FROM system_log WHERE timestamp < :expireTime AND action_type NOT IN ('DELETE', 'FORCE_UPDATE')")
    void deleteExpiredLogs(long expireTime);

    @Query("SELECT COUNT(*) FROM system_log")
    int getLogCount();
}