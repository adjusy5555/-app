package com.expressmanagement.app.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusUtil {

    // 状态常量
    public static final int STATUS_PENDING = 0;      // 待揽件
    public static final int STATUS_PICKED = 1;       // 已揽件
    public static final int STATUS_IN_TRANSIT = 2;   // 运输中
    public static final int STATUS_DELIVERING = 3;   // 派送中
    public static final int STATUS_DELIVERED = 4;    // 已签收
    public static final int STATUS_CANCELLED = 5;    // 已取消

    private static final Map<Integer, List<Integer>> allowedTransitions = new HashMap<>();

    static {
        // 定义合法的状态流转规则
        allowedTransitions.put(STATUS_PENDING, Arrays.asList(STATUS_PICKED, STATUS_CANCELLED));
        allowedTransitions.put(STATUS_PICKED, Arrays.asList(STATUS_IN_TRANSIT));
        allowedTransitions.put(STATUS_IN_TRANSIT, Arrays.asList(STATUS_DELIVERING));
        allowedTransitions.put(STATUS_DELIVERING, Arrays.asList(STATUS_DELIVERED));
        // 已签收和已取消是终态，不能再转换
    }

    /**
     * 验证状态流转是否合法
     */
    public static boolean canUpdateStatus(int currentStatus, int newStatus) {
        List<Integer> allowed = allowedTransitions.get(currentStatus);
        return allowed != null && allowed.contains(newStatus);
    }

    /**
     * 获取状态名称
     */
    public static String getStatusName(int status) {
        switch (status) {
            case STATUS_PENDING: return "待揽件";
            case STATUS_PICKED: return "已揽件";
            case STATUS_IN_TRANSIT: return "运输中";
            case STATUS_DELIVERING: return "派送中";
            case STATUS_DELIVERED: return "已签收";
            case STATUS_CANCELLED: return "已取消";
            default: return "未知状态";
        }
    }

    /**
     * 获取状态对应的物流描述
     */
    public static String getLogisticsDescription(int status, String courierName) {
        switch (status) {
            case STATUS_PENDING:
                return "订单已创建，等待快递员揽件";
            case STATUS_PICKED:
                return "快递员" + (courierName != null ? courierName : "") + "已揽件";
            case STATUS_IN_TRANSIT:
                return "包裹运输中";
            case STATUS_DELIVERING:
                return "快递员正在配送中，请保持电话畅通";
            case STATUS_DELIVERED:
                return "包裹已签收，感谢使用速通快递";
            case STATUS_CANCELLED:
                return "订单已取消";
            default:
                return "状态未知";
        }
    }

    /**
     * 判断是否可以取消订单
     */
    public static boolean canCancel(int status) {
        return status == STATUS_PENDING;
    }

    /**
     * 判断是否可以确认收货
     */
    public static boolean canConfirmReceipt(int status) {
        return status == STATUS_DELIVERING;
    }

    /**
     * 判断订单是否已完结
     */
    public static boolean isFinished(int status) {
        return status == STATUS_DELIVERED || status == STATUS_CANCELLED;
    }
}