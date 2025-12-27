package com.expressmanagement.app.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.LogisticsInfo;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.utils.StatusUtil;
import com.expressmanagement.app.utils.SystemLogHelper;

import java.util.List;

/**
 * 自动签收定时任务
 * 每天凌晨2点执行，将超过7天未确认的"派送中"订单自动签收
 */
public class AutoSignWorker extends Worker {

    private static final int AUTO_SIGN_DAYS = 7; // 7天自动签收

    public AutoSignWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            
            // 计算7天前的时间戳
            long sevenDaysAgo = System.currentTimeMillis() - (AUTO_SIGN_DAYS * 24L * 60 * 60 * 1000);
            
            // 查询所有需要自动签收的订单（状态为派送中且超过7天）
            List<Package> expiredPackages = db.packageDao().getExpiredPackages(
                    StatusUtil.STATUS_DELIVERING,
                    sevenDaysAgo
            );
            
            int autoSignCount = 0;
            
            for (Package pkg : expiredPackages) {
                // 更新订单状态为已签收
                pkg.setStatus(StatusUtil.STATUS_DELIVERED);
                pkg.setUpdateTime(System.currentTimeMillis());
                db.packageDao().update(pkg);
                
                // 添加物流轨迹
                LogisticsInfo info = new LogisticsInfo(
                        pkg.getPid(),
                        "系统自动签收（超时未确认收货）"
                );
                db.logisticsInfoDao().insert(info);
                
                // 记录系统日志
                SystemLogHelper.logAutoSign(
                        getApplicationContext(),
                        pkg.getPid(),
                        pkg.getTrackingNo()
                );
                
                autoSignCount++;
            }
            
            // 记录执行结果
            if (autoSignCount > 0) {
                android.util.Log.i("AutoSignWorker", 
                    "自动签收任务完成，处理了 " + autoSignCount + " 个订单");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            android.util.Log.e("AutoSignWorker", "自动签收任务失败", e);
            return Result.retry();
        }
    }
}