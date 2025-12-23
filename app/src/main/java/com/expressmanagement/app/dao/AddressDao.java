package com.expressmanagement.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.expressmanagement.app.entity.Address;

import java.util.List;

@Dao
public interface AddressDao {

    @Insert
    long insert(Address address);

    @Update
    void update(Address address);

    @Delete
    void delete(Address address);

    @Query("SELECT * FROM address WHERE aid = :aid")
    Address getAddressById(int aid);

    @Query("SELECT * FROM address WHERE user_id = :userId ORDER BY is_default DESC, create_time DESC")
    List<Address> getUserAddresses(int userId);

    @Query("SELECT * FROM address WHERE user_id = :userId AND is_default = 1 LIMIT 1")
    Address getDefaultAddress(int userId);

    @Query("UPDATE address SET is_default = 0 WHERE user_id = :userId")
    void clearDefaultAddress(int userId);

    @Query("SELECT COUNT(*) FROM address WHERE user_id = :userId")
    int getAddressCount(int userId);

    @Query("DELETE FROM address WHERE aid = :aid")
    void deleteById(int aid);
}