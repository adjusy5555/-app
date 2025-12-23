package com.expressmanagement.app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.expressmanagement.app.dao.AddressDao;
import com.expressmanagement.app.dao.LogisticsInfoDao;
import com.expressmanagement.app.dao.PackageDao;
import com.expressmanagement.app.dao.SystemLogDao;
import com.expressmanagement.app.dao.UserDao;
import com.expressmanagement.app.entity.Address;
import com.expressmanagement.app.entity.LogisticsInfo;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.entity.SystemLog;
import com.expressmanagement.app.entity.User;

@Database(entities = {User.class, Package.class, Address.class, 
                      LogisticsInfo.class, SystemLog.class}, 
          version = 1, 
          exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "express_management.db";
    private static volatile AppDatabase instance;
    
    public abstract UserDao userDao();
    public abstract PackageDao packageDao();
    public abstract AddressDao addressDao();
    public abstract LogisticsInfoDao logisticsInfoDao();
    public abstract SystemLogDao systemLogDao();
    
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .allowMainThreadQueries() // 注意：生产环境应移除，使用异步操作
                    .fallbackToDestructiveMigration()
                    .build();
                    
                    // 初始化管理员账号
                    initAdminAccount(instance);
                }
            }
        }
        return instance;
    }
    
    private static void initAdminAccount(AppDatabase db) {
        // 检查是否已存在管理员账号
        User existingAdmin = db.userDao().getUserByUsername("admin");
        if (existingAdmin == null) {
            // 创建默认管理员账号
            User admin = new User("admin", "admin123", "10000000000", 2);
            admin.setNickname("系统管理员");
            db.userDao().insert(admin);
            
            // 记录系统日志
            SystemLog log = new SystemLog(
                0, 2, "INIT", "User", null, 
                "系统初始化：创建默认管理员账号"
            );
            db.systemLogDao().insert(log);
        }
    }
}