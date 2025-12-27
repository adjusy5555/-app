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

    @Query("UPDATE user SET status = 1 WHERE uid = :uid")
    void softDelete(int uid);

    @Query("SELECT * FROM user WHERE uid = :uid")
    User getUserById(int uid);

    @Query("SELECT * FROM user WHERE username = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM user WHERE username = :username AND password = :password AND status = 0")
    User login(String username, String password);

    @Query("SELECT * FROM user WHERE phone = :phone AND status = 0")
    User getUserByPhone(String phone);

    @Query("SELECT * FROM user WHERE status = 0")
    List<User> getAllUsers();

    @Query("SELECT * FROM user WHERE role = :role AND status = 0")
    List<User> getUsersByRole(int role);

    @Query("SELECT COUNT(*) FROM user WHERE username = :username")
    int checkUsernameExists(String username);

    @Query("SELECT COUNT(*) FROM user WHERE phone = :phone")
    int checkPhoneExists(String phone);

    @Query("UPDATE user SET password = :newPassword WHERE uid = :uid")
    void updatePassword(int uid, String newPassword);

    @Query("DELETE FROM user WHERE uid = :uid")
    void deleteById(int uid);
}