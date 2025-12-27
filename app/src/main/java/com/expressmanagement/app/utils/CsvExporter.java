package com.expressmanagement.app.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.expressmanagement.app.entity.SystemLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * CSV 文件导出工具
 */
public class CsvExporter {

    /**
     * 导出系统日志到CSV文件
     * 
     * @param context 上下文
     * @param logs 日志列表
     * @return 导出成功返回文件路径，失败返回null
     */
    public static String exportSystemLogs(Context context, List<SystemLog> logs) {
        if (logs == null || logs.isEmpty()) {
            Toast.makeText(context, "没有可导出的数据", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            // 创建文件
            File exportDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "ExpressManagement"
            );
            
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            String fileName = "system_logs_" + System.currentTimeMillis() + ".csv";
            File file = new File(exportDir, fileName);

            FileWriter writer = new FileWriter(file);

            // 写入 CSV 头部（带 BOM 解决中文乱码）
            writer.write("\uFEFF"); // BOM for UTF-8
            writer.write("日志ID,操作者ID,操作者角色,操作类型,目标类型,目标ID,操作描述,操作时间\n");

            // 写入数据行
            for (SystemLog log : logs) {
                writer.write(formatCsvLine(
                        String.valueOf(log.getLid()),
                        String.valueOf(log.getOperatorId()),
                        getRoleName(log.getOperatorRole()),
                        getActionTypeName(log.getActionType()),
                        log.getTargetType(),
                        log.getTargetId() != null ? String.valueOf(log.getTargetId()) : "",
                        escapeCsvValue(log.getDescription()),
                        TimeUtil.formatDateTime(log.getTimestamp())
                ));
            }

            writer.flush();
            writer.close();

            return file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * 格式化CSV行数据
     */
    private static String formatCsvLine(String... values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                line.append(",");
            }
            line.append(values[i]);
        }
        line.append("\n");
        return line.toString();
    }

    /**
     * 转义CSV值（处理包含逗号、引号、换行符的情况）
     */
    private static String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        
        // 如果包含逗号、引号或换行符，需要用引号包裹
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // 引号需要转义为两个引号
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        
        return value;
    }

    /**
     * 获取角色名称
     */
    private static String getRoleName(int role) {
        switch (role) {
            case 0: return "普通用户";
            case 1: return "快递员";
            case 2: return "管理员";
            default: return "系统";
        }
    }

    /**
     * 获取操作类型名称
     */
    private static String getActionTypeName(String actionType) {
        switch (actionType) {
            case "LOGIN": return "登录";
            case "REGISTER": return "注册";
            case "CREATE_PACKAGE": return "创建订单";
            case "UPDATE_STATUS": return "更新状态";
            case "CANCEL_PACKAGE": return "取消订单";
            case "PICKUP": return "接单";
            case "CHANGE_PASSWORD": return "修改密码";
            case "CREATE": return "创建";
            case "UPDATE": return "更新";
            case "DELETE": return "删除";
            case "INIT": return "初始化";
            case "AUTO_SIGN": return "自动签收";
            default:
                if (actionType.startsWith("ADMIN_")) {
                    return "管理员操作";
                }
                return actionType;
        }
    }

    /**
     * 导出订单数据到CSV（供管理员使用）
     */
    public static String exportPackages(Context context, List<com.expressmanagement.app.entity.Package> packages) {
        if (packages == null || packages.isEmpty()) {
            Toast.makeText(context, "没有可导出的数据", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            File exportDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "ExpressManagement"
            );
            
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            String fileName = "packages_" + System.currentTimeMillis() + ".csv";
            File file = new File(exportDir, fileName);

            FileWriter writer = new FileWriter(file);

            // 写入 CSV 头部
            writer.write("\uFEFF"); // BOM for UTF-8
            writer.write("订单ID,运单号,寄件人ID,收件人姓名,收件人电话,物品类型,重量(kg),运费(元),状态,快递员ID,创建时间,更新时间,备注\n");

            // 写入数据行
            for (com.expressmanagement.app.entity.Package pkg : packages) {
                writer.write(formatCsvLine(
                        String.valueOf(pkg.getPid()),
                        pkg.getTrackingNo(),
                        String.valueOf(pkg.getSenderId()),
                        escapeCsvValue(pkg.getReceiverName()),
                        pkg.getReceiverPhone(),
                        pkg.getItemType(),
                        String.valueOf(pkg.getWeight()),
                        String.valueOf(pkg.getPrice()),
                        pkg.getStatusName(),
                        pkg.getCourierId() != null ? String.valueOf(pkg.getCourierId()) : "",
                        TimeUtil.formatDateTime(pkg.getCreateTime()),
                        TimeUtil.formatDateTime(pkg.getUpdateTime()),
                        escapeCsvValue(pkg.getRemark())
                ));
            }

            writer.flush();
            writer.close();

            return file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}