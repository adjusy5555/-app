package com.expressmanagement.app.utils;

import android.content.Context;

import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.SystemLog;

public class SystemLogHelper {

    /**
     * 记录系统日志
     */
    public static void log(Context context, int operatorId, int operatorRole,
                           String actionType, String targetType,
                           Integer targetId, String description) {
        SystemLog log = new SystemLog(operatorId, operatorRole, actionType,
                targetType, targetId, description);
        AppDatabase.getInstance(context).systemLogDao().insert(log);
    }

    /**
     * 记录用户登录日志
     */
    public static void logLogin(Context context, int userId, int role) {
        log(context, userId, role, "LOGIN", "User", userId, "用户登录");
    }

    /**
     * 记录用户注册日志
     */
    public static void logRegister(Context context, int userId, int role) {
        log(context, userId, role, "REGISTER", "User", userId, "用户注册");
    }

    /**
     * 记录订单创建日志
     */
    public static void logCreatePackage(Context context, int userId, int packageId, String trackingNo) {
        log(context, userId, 0, "CREATE_PACKAGE", "Package", packageId,
                "创建订单: " + trackingNo);
    }

    /**
     * 记录订单状态更新日志
     */
    public static void logUpdatePackageStatus(Context context, int operatorId, int role,
                                              int packageId, String trackingNo,
                                              int oldStatus, int newStatus) {
        String description = String.format("更新订单状态: %s [%s -> %s]",
                trackingNo,
                StatusUtil.getStatusName(oldStatus),
                StatusUtil.getStatusName(newStatus));
        log(context, operatorId, role, "UPDATE_STATUS", "Package", packageId, description);
    }

    /**
     * 记录订单取消日志
     */
    public static void logCancelPackage(Context context, int userId, int packageId, String trackingNo) {
        log(context, userId, 0, "CANCEL_PACKAGE", "Package", packageId,
                "取消订单: " + trackingNo);
    }

    /**
     * 记录快递员接单日志
     */
    public static void logPickupPackage(Context context, int courierId, int packageId, String trackingNo) {
        log(context, courierId, 1, "PICKUP", "Package", packageId,
                "接单: " + trackingNo);
    }

    /**
     * 记录地址操作日志
     */
    public static void logAddressAction(Context context, int userId, String action, int addressId) {
        log(context, userId, 0, action, "Address", addressId,
                "地址" + action);
    }

    /**
     * 记录管理员强制操作日志
     */
    public static void logAdminForceAction(Context context, int adminId, String action,
                                           String targetType, int targetId, String details) {
        log(context, adminId, 2, "ADMIN_" + action, targetType, targetId,
                "管理员操作: " + details);
    }

    /**
     * 记录密码修改日志
     */
    public static void logPasswordChange(Context context, int userId, int role) {
        log(context, userId, role, "CHANGE_PASSWORD", "User", userId, "修改密码");
    }

    /**
     * 记录自动签收日志
     */
    public static void logAutoSign(Context context, int packageId, String trackingNo) {
        log(context, 0, 0, "AUTO_SIGN", "Package", packageId,
                "系统自动签收订单: " + trackingNo);
    }
}