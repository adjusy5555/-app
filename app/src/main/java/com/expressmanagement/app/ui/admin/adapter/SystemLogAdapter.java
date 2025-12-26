package com.expressmanagement.app.ui.admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.SystemLog;
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.utils.TimeUtil;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统日志列表适配器
 */
public class SystemLogAdapter extends RecyclerView.Adapter<SystemLogAdapter.ViewHolder> {

    private final Context context;
    private List<SystemLog> logs = new ArrayList<>();
    private final AppDatabase db;

    public SystemLogAdapter(Context context) {
        this.context = context;
        this.db = AppDatabase.getInstance(context);
    }

    public void setLogs(List<SystemLog> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_system_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SystemLog log = logs.get(position);
        
        // 获取操作者信息
        String operatorInfo = getOperatorInfo(log.getOperatorId(), log.getOperatorRole());
        holder.tvOperator.setText(operatorInfo);
        
        // 设置操作类型
        holder.tvActionType.setText(getActionTypeName(log.getActionType()));
        
        // 设置描述
        holder.tvDescription.setText(log.getDescription());
        
        // 设置时间
        holder.tvTime.setText(TimeUtil.formatDateTime(log.getTimestamp()));
        
        // 设置卡片颜色（根据操作类型）
        int cardColor = getActionTypeColor(log.getActionType());
        holder.cardView.setCardBackgroundColor(cardColor);
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    private String getOperatorInfo(int operatorId, int operatorRole) {
        if (operatorId == 0) {
            return "系统";
        }
        
        User user = db.userDao().getUserById(operatorId);
        if (user == null) {
            return "未知用户 (ID: " + operatorId + ")";
        }
        
        String roleName = "";
        switch (operatorRole) {
            case 0: roleName = "用户"; break;
            case 1: roleName = "快递员"; break;
            case 2: roleName = "管理员"; break;
        }
        
        return user.getUsername() + " (" + roleName + ")";
    }

    private String getActionTypeName(String actionType) {
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

    private int getActionTypeColor(String actionType) {
        switch (actionType) {
            case "LOGIN":
            case "REGISTER":
                return 0xFFE3F2FD; // 浅蓝色
            
            case "CREATE_PACKAGE":
            case "CREATE":
                return 0xFFE8F5E9; // 浅绿色
            
            case "UPDATE_STATUS":
            case "UPDATE":
                return 0xFFFFF3E0; // 浅橙色
            
            case "CANCEL_PACKAGE":
            case "DELETE":
                return 0xFFFFEBEE; // 浅红色
            
            case "PICKUP":
                return 0xFFF3E5F5; // 浅紫色
            
            default:
                if (actionType.startsWith("ADMIN_")) {
                    return 0xFFFFF9C4; // 浅黄色
                }
                return 0xFFF5F5F5; // 浅灰色
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvOperator;
        TextView tvActionType;
        TextView tvDescription;
        TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tvOperator = itemView.findViewById(R.id.tvOperator);
            tvActionType = itemView.findViewById(R.id.tvActionType);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}