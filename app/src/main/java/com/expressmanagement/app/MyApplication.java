package com.expressmanagement.app;

import android.app.Application;
import com.expressmanagement.app.database.AppDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化数据库
        AppDatabase.getInstance(this);
    }
}