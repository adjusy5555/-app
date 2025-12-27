package com.expressmanagement.app.utils;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.expressmanagement.app.worker.AutoSignWorker;

import java.util.concurrent.TimeUnit;

/**
 * WorkManager 定时任务管理器
 */
public class WorkManagerHelper {

    private static final String AUTO_SIGN_WORK_NAME = "auto_sign_worker";

    /**
     * 初始化自动签收定时任务
     * 每天凌晨2点执行一次
     */
    public static void setupAutoSignWork(Context context) {
        // 设置约束条件
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false) // 不需要充电
                .setRequiresBatteryNotLow(true) // 电量不能太低
                .build();

        // 创建周期性任务请求（每24小时执行一次）
        PeriodicWorkRequest autoSignRequest = new PeriodicWorkRequest.Builder(
                AutoSignWorker.class,
                24, TimeUnit.HOURS, // 重复间隔
                15, TimeUnit.MINUTES // 灵活时间窗口
        )
                .setConstraints(constraints)
                .addTag("auto_sign")
                .build();

        // 将任务加入队列
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                AUTO_SIGN_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, // 如果已存在则保持
                autoSignRequest
        );
    }

    /**
     * 取消自动签收任务
     */
    public static void cancelAutoSignWork(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(AUTO_SIGN_WORK_NAME);
    }

    /**
     * 手动触发一次自动签收任务（用于测试）
     */
    public static void triggerAutoSignNow(Context context) {
        // 创建一次性任务
        androidx.work.OneTimeWorkRequest oneTimeWork = 
            new androidx.work.OneTimeWorkRequest.Builder(AutoSignWorker.class)
                .addTag("auto_sign_manual")
                .build();
        
        WorkManager.getInstance(context).enqueue(oneTimeWork);
    }
}