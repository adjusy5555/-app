package com.expressmanagement.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class TrackingNoUtil {

    /**
     * 生成运单号
     * 格式: ST + 年月日时分秒 + 4位随机数
     * 例如: ST202412231430251234
     */
    public static String generateTrackingNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        String random = String.format(Locale.getDefault(), "%04d", new Random().nextInt(10000));
        return "ST" + timestamp + random;
    }

    /**
     * 验证运单号格式是否正确
     */
    public static boolean isValidTrackingNo(String trackingNo) {
        if (trackingNo == null || trackingNo.length() != 20) {
            return false;
        }
        return trackingNo.startsWith("ST");
    }

    /**
     * 格式化运单号显示（分段显示）
     * ST202412231430251234 -> ST-20241223-1430-251234
     */
    public static String formatTrackingNo(String trackingNo) {
        if (!isValidTrackingNo(trackingNo)) {
            return trackingNo;
        }
        String prefix = trackingNo.substring(0, 2);
        String date = trackingNo.substring(2, 10);
        String time = trackingNo.substring(10, 14);
        String random = trackingNo.substring(14);
        return prefix + "-" + date + "-" + time + "-" + random;
    }
}