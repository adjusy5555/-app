package com.expressmanagement.app.ui.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Address;
import com.expressmanagement.app.entity.LogisticsInfo;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.utils.PriceCalculator;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.StatusUtil;
import com.expressmanagement.app.utils.SystemLogHelper;
import com.expressmanagement.app.utils.TimeUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员订单列表适配器
 */
public class AdminPackageAdapter extends RecyclerView.Adapter<AdminPackageAdapter.ViewHolder> {

    private final Context context;
    private List<Package> packages = new ArrayList<>();
    private final AppDatabase db;

    public AdminPackageAdapter(Context context) {
        this.context = context;
        this.db = AppDatabase.getInstance(context);
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_package, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Package pkg = packages.get(position);
        
        // 设置基本信息
        holder.tvStatus.setText(pkg.getStatusName());
        holder.tvTrackingNo.setText(pkg.getTrackingNo());
        holder.tvReceiverName.setText(pkg.getReceiverName());
        holder.tvReceiverPhone.setText(pkg.getReceiverPhone());
        
        // 获取地址信息
        Address address = db.addressDao().getAddressById(pkg.getAddressId());
        if (address != null) {
            holder.tvAddress.setText(address.getFullAddress());
        }
        
        holder.tvItemType.setText(pkg.getItemType());
        holder.tvWeight.setText(pkg.getWeight() + "kg");
        holder.tvPrice.setText("¥" + PriceCalculator.formatPrice(pkg.getPrice()));
        holder.tvTime.setText(TimeUtil.formatDateTime(pkg.getCreateTime()));
        
        // 设置状态颜色
        int statusColor = getStatusColor(pkg.getStatus());
        holder.tvStatus.setBackgroundColor(statusColor);
        
        // 管理员可以查看详情和强制操作
        holder.layoutActions.setVisibility(View.VISIBLE);
        holder.btnCancel.setVisibility(View.GONE);
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnDetail.setVisibility(View.VISIBLE);
        
        // 查看详情
        holder.btnDetail.setOnClickListener(v -> {
            // 跳转到详情页面
            Toast.makeText(context, "查看订单详情: " + pkg.getTrackingNo(), Toast.LENGTH_SHORT).show();
        });
        
        // 长按显示强制操作
        holder.itemView.setOnLongClickListener(v -> {
            showAdminActions(pkg);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    private int getStatusColor(int status) {
        switch (status) {
            case StatusUtil.STATUS_PENDING: return 0xFF999999;
            case StatusUtil.STATUS_PICKED: return 0xFF2196F3;
            case StatusUtil.STATUS_IN_TRANSIT: return 0xFFFF9800;
            case StatusUtil.STATUS_DELIVERING: return 0xFF9C27B0;
            case StatusUtil.STATUS_DELIVERED: return 0xFF4CAF50;
            case StatusUtil.STATUS_CANCELLED: return 0xFFF44336;
            default: return 0xFF999999;
        }
    }

    private void showAdminActions(Package pkg) {
        String[] actions;
        
        if (StatusUtil.isFinished(pkg.getStatus())) {
            actions = new String[]{"查看详情", "删除订单"};
        } else {
            actions = new String[]{"查看详情", "强制完成", "强制取消", "删除订单"};
        }
        
        new AlertDialog.Builder(context)
                .setTitle("管理员操作")
                .setItems(actions, (dialog, which) -> {
                    switch (which) {
                        case 0: // 查看详情
                            Toast.makeText(context, "查看详情功能开发中", Toast.LENGTH_SHORT).show();
                            break;
                        case 1: // 强制完成/删除订单
                            if (StatusUtil.isFinished(pkg.getStatus())) {
                                showDeleteDialog(pkg);
                            } else {
                                forceComplete(pkg);
                            }
                            break;
                        case 2: // 强制取消
                            forceCancel(pkg);
                            break;
                        case 3: // 删除订单
                            showDeleteDialog(pkg);
                            break;
                    }
                })
                .show();
    }

    private void forceComplete(Package pkg) {
        new AlertDialog.Builder(context)
                .setTitle("强制完成")
                .setMessage("确定要强制将此订单标记为已完成吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    int oldStatus = pkg.getStatus();
                    pkg.setStatus(StatusUtil.STATUS_DELIVERED);
                    pkg.setUpdateTime(System.currentTimeMillis());
                    db.packageDao().update(pkg);
                    
                    // 添加物流信息
                    LogisticsInfo info = new LogisticsInfo(pkg.getPid(), "管理员强制完成订单");
                    db.logisticsInfoDao().insert(info);
                    
                    // 记录日志
                    int adminId = PreferencesUtil.getCurrentUserId(context);
                    SystemLogHelper.logUpdatePackageStatus(
                            context, adminId, 2, pkg.getPid(),
                            pkg.getTrackingNo(), oldStatus, pkg.getStatus()
                    );
                    
                    notifyDataSetChanged();
                    Toast.makeText(context, "订单已强制完成", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void forceCancel(Package pkg) {
        new AlertDialog.Builder(context)
                .setTitle("强制取消")
                .setMessage("确定要强制取消此订单吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    int oldStatus = pkg.getStatus();
                    pkg.setStatus(StatusUtil.STATUS_CANCELLED);
                    pkg.setUpdateTime(System.currentTimeMillis());
                    db.packageDao().update(pkg);
                    
                    // 添加物流信息
                    LogisticsInfo info = new LogisticsInfo(pkg.getPid(), "管理员强制取消订单");
                    db.logisticsInfoDao().insert(info);
                    
                    // 记录日志
                    int adminId = PreferencesUtil.getCurrentUserId(context);
                    SystemLogHelper.logUpdatePackageStatus(
                            context, adminId, 2, pkg.getPid(),
                            pkg.getTrackingNo(), oldStatus, pkg.getStatus()
                    );
                    
                    notifyDataSetChanged();
                    Toast.makeText(context, "订单已强制取消", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showDeleteDialog(Package pkg) {
        new AlertDialog.Builder(context)
                .setTitle("删除订单")
                .setMessage("确定要删除此订单吗？相关的物流信息也会被删除，此操作不可恢复！")
                .setPositiveButton("确定", (dialog, which) -> deletePackage(pkg))
                .setNegativeButton("取消", null)
                .show();
    }

    private void deletePackage(Package pkg) {
        // 删除物流信息
        db.logisticsInfoDao().deleteByPackageId(pkg.getPid());
        
        // 删除订单
        db.packageDao().delete(pkg);
        
        // 记录日志
        int adminId = PreferencesUtil.getCurrentUserId(context);
        SystemLogHelper.logAdminForceAction(
                context,
                adminId,
                "DELETE_PACKAGE",
                "Package",
                pkg.getPid(),
                "删除订单: " + pkg.getTrackingNo()
        );
        
        // 从列表中移除
        packages.remove(pkg);
        notifyDataSetChanged();
        
        Toast.makeText(context, "订单已删除", Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus;
        TextView tvTrackingNo;
        TextView tvReceiverName;
        TextView tvReceiverPhone;
        TextView tvAddress;
        TextView tvItemType;
        TextView tvWeight;
        TextView tvPrice;
        TextView tvTime;
        LinearLayout layoutActions;
        MaterialButton btnCancel;
        MaterialButton btnConfirm;
        MaterialButton btnDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTrackingNo = itemView.findViewById(R.id.tvTrackingNo);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvReceiverPhone = itemView.findViewById(R.id.tvReceiverPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvItemType = itemView.findViewById(R.id.tvItemType);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTime = itemView.findViewById(R.id.tvTime);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}