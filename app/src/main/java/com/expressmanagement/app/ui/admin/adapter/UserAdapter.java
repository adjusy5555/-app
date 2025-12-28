package com.expressmanagement.app.ui.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.SystemLogHelper;
import com.expressmanagement.app.utils.TimeUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户列表适配器
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context context;
    private List<User> users = new ArrayList<>();
    private final AppDatabase db;

    public UserAdapter(Context context) {
        this.context = context;
        this.db = AppDatabase.getInstance(context);
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        holder.tvUsername.setText(user.getUsername());
        holder.tvRole.setText(user.getRoleName());
        holder.tvPhone.setText(user.getPhone());
        holder.tvCreateTime.setText("注册时间: " + TimeUtil.formatDate(user.getCreateTime()));

        // 设置角色标签颜色
        int roleColor = getRoleColor(user.getRole());
        holder.tvRole.setBackgroundColor(roleColor);

        // 重置密码
        holder.btnResetPassword.setOnClickListener(v -> showResetPasswordDialog(user));

        // 删除用户
        holder.btnDelete.setOnClickListener(v -> showDeleteDialog(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private int getRoleColor(int role) {
        switch (role) {
            case 0: return 0xFF2196F3; // 普通用户 - 蓝色
            case 1: return 0xFF4CAF50; // 快递员 - 绿色
            case 2: return 0xFFFF6F00; // 管理员 - 橙色
            default: return 0xFF999999;
        }
    }

    private void showResetPasswordDialog(User user) {
        new AlertDialog.Builder(context)
                .setTitle("重置密码")
                .setMessage("确定要将用户 " + user.getUsername() + " 的密码重置为 123456 吗？")
                .setPositiveButton("确定", (dialog, which) -> resetPassword(user))
                .setNegativeButton("取消", null)
                .show();
    }

    private void resetPassword(User user) {
        user.setPassword("123456");
        db.userDao().update(user);

        // 记录日志
        int adminId = PreferencesUtil.getCurrentUserId(context);
        SystemLogHelper.logAdminForceAction(
                context,
                adminId,
                "RESET_PASSWORD",
                "User",
                user.getUid(),
                "重置用户密码: " + user.getUsername()
        );

        Toast.makeText(context, "密码已重置为 123456", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteDialog(User user) {
        // 不允许删除管理员账号
        if (user.isAdmin()) {
            Toast.makeText(context, "不能删除管理员账号", Toast.LENGTH_SHORT).show();
            return;
        }

        // 不允许删除自己
        int currentUserId = PreferencesUtil.getCurrentUserId(context);
        if (user.getUid() == currentUserId) {
            Toast.makeText(context, "不能删除当前登录的账号", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle("删除用户")
                .setMessage("确定要删除用户 " + user.getUsername() + " 吗？此操作不可恢复！")
                .setPositiveButton("确定", (dialog, which) -> deleteUser(user))
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteUser(User user) {
        // 使用软删除：标记为已删除状态，而不是真的删除数据
        user.setStatus(1); // 1 表示已删除
        db.userDao().update(user);

        // 记录日志
        int adminId = PreferencesUtil.getCurrentUserId(context);
        SystemLogHelper.logAdminForceAction(
                context,
                adminId,
                "DELETE_USER",
                "User",
                user.getUid(),
                "删除用户: " + user.getUsername()
        );

        // 从列表中移除
        users.remove(user);
        notifyDataSetChanged();

        Toast.makeText(context, "用户已删除", Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvRole;
        TextView tvPhone;
        TextView tvCreateTime;
        MaterialButton btnResetPassword;
        MaterialButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvCreateTime = itemView.findViewById(R.id.tvCreateTime);
            btnResetPassword = itemView.findViewById(R.id.btnResetPassword);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}