package com.expressmanagement.app;

import android.app.Application;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.utils.WorkManagerHelper;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化数据库
        AppDatabase.getInstance(this);

        //初始化自动签收定时任务
        WorkManagerHelper.setupAutoSignWork(this);
    }
}