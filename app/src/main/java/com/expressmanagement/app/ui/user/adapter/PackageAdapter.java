package com.expressmanagement.app.ui.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Address;
import com.expressmanagement.app.entity.LogisticsInfo;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.utils.PriceCalculator;
import com.expressmanagement.app.utils.StatusUtil;
import com.expressmanagement.app.utils.SystemLogHelper;
import com.expressmanagement.app.utils.TimeUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 快递列表适配器
 */
public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    private Context context;
    private List<Package> packages = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private AppDatabase database;

    public interface OnItemClickListener {
        void onItemClick(Package pkg);
    }

    public PackageAdapter(Context context) {
        this.context = context;
        this.database = AppDatabase.getInstance(context);
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
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
        
        // 状态
        holder.tvStatus.setText(pkg.getStatusName());
        int statusColor = getStatusColor(pkg.getStatus());
        holder.tvStatus.setBackgroundColor(statusColor);
        
        // 运单号
        holder.tvTrackingNo.setText(pkg.getTrackingNo());
        
        // 收件人信息
        holder.tvReceiverName.setText(pkg.getReceiverName());
        holder.tvReceiverPhone.setText(pkg.getReceiverPhone());
        
        // 地址
        Address address = database.addressDao().getAddressById(pkg.getAddressId());
        if (address != null) {
            holder.tvAddress.setText(address.getFullAddress());
        }
        
        // 物品信息
        holder.tvItemType.setText(pkg.getItemType());
        holder.tvWeight.setText(pkg.getWeight() + "kg");
        holder.tvPrice.setText("¥" + PriceCalculator.formatPrice(pkg.getPrice()));
        
        // 时间
        holder.tvTime.setText(TimeUtil.formatDateTime(pkg.getCreateTime()));
        
        // 操作按钮
        setupActionButtons(holder, pkg);
        
        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(pkg);
            }
        });
    }

    private void setupActionButtons(ViewHolder holder, Package pkg) {
        holder.layoutActions.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnDetail.setVisibility(View.GONE);
        
        // 待揽件状态：显示取消按钮
        if (pkg.getStatus() == StatusUtil.STATUS_PENDING) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnDetail.setVisibility(View.VISIBLE);
            
            holder.btnCancel.setOnClickListener(v -> showCancelDialog(pkg));
        }
        // 派送中状态：显示确认收货按钮
        else if (pkg.getStatus() == StatusUtil.STATUS_DELIVERING) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnDetail.setVisibility(View.VISIBLE);
            
            holder.btnConfirm.setOnClickListener(v -> confirmReceipt(pkg));
        }
        // 其他状态：只显示查看详情
        else {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnDetail.setVisibility(View.VISIBLE);
        }
        
        holder.btnDetail.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(pkg);
            }
        });
    }

    private void showCancelDialog(Package pkg) {
        new AlertDialog.Builder(context)
                .setTitle("取消订单")
                .setMessage("确定要取消该订单吗？")
                .setPositiveButton("确定", (dialog, which) -> cancelPackage(pkg))
                .setNegativeButton("取消", null)
                .show();
    }

    private void cancelPackage(Package pkg) {
        // 更新状态为已取消
        pkg.setStatus(StatusUtil.STATUS_CANCELLED);
        pkg.setUpdateTime(System.currentTimeMillis());
        database.packageDao().update(pkg);
        
        // 添加物流轨迹
        LogisticsInfo logistics = new LogisticsInfo(pkg.getPid(), "订单已取消");
        database.logisticsInfoDao().insert(logistics);
        
        // 记录日志
        SystemLogHelper.logCancelPackage(context, pkg.getSenderId(), 
                pkg.getPid(), pkg.getTrackingNo());
        
        Toast.makeText(context, "订单已取消", Toast.LENGTH_SHORT).show();
        
        // 刷新列表
        notifyDataSetChanged();
    }

    private void confirmReceipt(Package pkg) {
        new AlertDialog.Builder(context)
                .setTitle("确认收货")
                .setMessage("确认已收到快递？")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 更新状态为已签收
                    pkg.setStatus(StatusUtil.STATUS_DELIVERED);
                    pkg.setUpdateTime(System.currentTimeMillis());
                    database.packageDao().update(pkg);
                    
                    // 添加物流轨迹
                    LogisticsInfo logistics = new LogisticsInfo(pkg.getPid(), "用户已确认收货");
                    database.logisticsInfoDao().insert(logistics);
                    
                    // 记录日志
                    SystemLogHelper.logUpdatePackageStatus(context, pkg.getSenderId(), 0,
                            pkg.getPid(), pkg.getTrackingNo(), 
                            StatusUtil.STATUS_DELIVERING, StatusUtil.STATUS_DELIVERED);
                    
                    Toast.makeText(context, "已确认收货", Toast.LENGTH_SHORT).show();
                    
                    // 刷新列表
                    notifyDataSetChanged();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private int getStatusColor(int status) {
        switch (status) {
            case StatusUtil.STATUS_PENDING:
                return 0xFFFF9800; // 橙色
            case StatusUtil.STATUS_PICKED:
                return 0xFF2196F3; // 蓝色
            case StatusUtil.STATUS_IN_TRANSIT:
                return 0xFF9C27B0; // 紫色
            case StatusUtil.STATUS_DELIVERING:
                return 0xFF4CAF50; // 绿色
            case StatusUtil.STATUS_DELIVERED:
                return 0xFF757575; // 灰色
            case StatusUtil.STATUS_CANCELLED:
                return 0xFFF44336; // 红色
            default:
                return 0xFF9E9E9E;
        }
    }

    @Override
    public int getItemCount() {
        return packages.size();
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