package com.expressmanagement.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.expressmanagement.app.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    /**
     * 根据 ID 查询用户（包含已删除）
     */
    @Query("SELECT * FROM user WHERE uid = :uid")
    User getUserById(int uid);

    /**
     * 根据用户名查询用户（仅正常状态）
     */
    @Query("SELECT * FROM user WHERE username = :username AND status = 0")
    User getUserByUsername(String username);

    /**
     * 登录查询（仅正常状态的用户可以登录）
     */
    @Query("SELECT * FROM user WHERE username = :username AND password = :password AND status = 0")
    User login(String username, String password);

    /**
     * 根据手机号查询用户（仅正常状态）
     */
    @Query("SELECT * FROM user WHERE phone = :phone AND status = 0")
    User getUserByPhone(String phone);

    /**
     * 获取所有正常状态的用户
     */
    @Query("SELECT * FROM user WHERE status = 0")
    List<User> getAllUsers();

    /**
     * 根据角色获取用户（仅正常状态）
     */
    @Query("SELECT * FROM user WHERE role = :role AND status = 0")
    List<User> getUsersByRole(int role);

    /**
     * 检查用户名是否存在（仅检查正常状态的用户）
     */
    @Query("SELECT COUNT(*) FROM user WHERE username = :username AND status = 0")
    int checkUsernameExists(String username);

    /**
     * 检查手机号是否存在（仅检查正常状态的用户）
     */
    @Query("SELECT COUNT(*) FROM user WHERE phone = :phone AND status = 0")
    int checkPhoneExists(String phone);

    /**
     * 更新密码
     */
    @Query("UPDATE user SET password = :newPassword WHERE uid = :uid")
    void updatePassword(int uid, String newPassword);

    /**
     * 物理删除用户（慎用）
     */
    @Query("DELETE FROM user WHERE uid = :uid")
    void deleteById(int uid);
}