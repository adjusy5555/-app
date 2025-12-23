package com.expressmanagement.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    /**
     * 格式化时间戳为日期时间字符串
     * 格式: yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * 格式化时间戳为日期字符串
     * 格式: yyyy-MM-dd
     */
    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * 格式化时间戳为时间字符串
     * 格式: HH:mm:ss
     */
    public static String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * 格式化时间戳为简短格式
     * 格式: MM-dd HH:mm
     */
    public static String formatShortDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * 获取今天0点的时间戳
     */
    public static long getTodayStartTime() {
        long currentTime = System.currentTimeMillis();
        return currentTime - (currentTime % (24 * 60 * 60 * 1000))
                - (8 * 60 * 60 * 1000); // 减去时区偏移
    }

    /**
     * 获取指定天数前的时间戳
     */
    public static long getDaysAgo(int days) {
        return System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);
    }

    /**
     * 判断时间戳是否已过期（超过指定天数）
     */
    public static boolean isExpired(long timestamp, int days) {
        long expireTime = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);
        return timestamp < expireTime;
    }

    /**
     * 计算两个时间戳之间的天数差
     */
    public static int getDaysBetween(long startTime, long endTime) {
        return (int) ((endTime - startTime) / (24 * 60 * 60 * 1000));
    }
}