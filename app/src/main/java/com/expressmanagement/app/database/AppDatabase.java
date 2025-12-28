package com.expressmanagement.app.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
        version = 2,  //版本号
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "express_management.db";
    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract PackageDao packageDao();
    public abstract AddressDao addressDao();
    public abstract LogisticsInfoDao logisticsInfoDao();
    public abstract SystemLogDao systemLogDao();

    /**
     * 数据库迁移：版本 1 -> 版本 2
     * 添加 user 表的 status 字段
     */
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 为 user 表添加 status 字段，默认值为 0（正常状态）
            database.execSQL("ALTER TABLE user ADD COLUMN status INTEGER NOT NULL DEFAULT 0");
        }
    };

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
                            .addMigrations(MIGRATION_1_2) // ⭐ 添加迁移策略
                            .fallbackToDestructiveMigration() // 如果迁移失败，则重建数据库
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
            admin.setStatus(0); // 设置为正常状态
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