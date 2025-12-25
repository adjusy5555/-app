package com.expressmanagement.app.ui.courier.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Address;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.utils.StatusUtil;
import com.expressmanagement.app.utils.TimeUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 配送订单适配器
 */
public class DeliveryPackageAdapter extends RecyclerView.Adapter<DeliveryPackageAdapter.ViewHolder> {

    private Context context;
    private List<Package> packages = new ArrayList<>();
    private OnCallClickListener onCallClickListener;
    private OnUpdateStatusClickListener onUpdateStatusClickListener;
    private AppDatabase database;

    public interface OnCallClickListener {
        void onCallClick(Package pkg);
    }

    public interface OnUpdateStatusClickListener {
        void onUpdateStatusClick(Package pkg);
    }

    public DeliveryPackageAdapter(Context context) {
        this.context = context;
        this.database = AppDatabase.getInstance(context);
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
        notifyDataSetChanged();
    }

    public void setOnCallClickListener(OnCallClickListener listener) {
        this.onCallClickListener = listener;
    }

    public void setOnUpdateStatusClickListener(OnUpdateStatusClickListener listener) {
        this.onUpdateStatusClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_package, parent, false);
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
        
        // 时间
        holder.tvTime.setText("接单时间：" + TimeUtil.formatDateTime(pkg.getUpdateTime()));
        
        // 操作按钮
        setupActionButtons(holder, pkg);
    }

    private void setupActionButtons(ViewHolder holder, Package pkg) {
        holder.layoutActions.setVisibility(View.VISIBLE);
        
        // 拨打电话按钮（所有状态都显示）
        holder.btnCall.setVisibility(View.VISIBLE);
        holder.btnCall.setOnClickListener(v -> {
            if (onCallClickListener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onCallClickListener.onCallClick(packages.get(pos));
                }
            }
        });
        
        // 更新状态按钮（派送中状态不显示）
        if (pkg.getStatus() == StatusUtil.STATUS_DELIVERING) {
            holder.btnUpdateStatus.setVisibility(View.GONE);
        } else {
            holder.btnUpdateStatus.setVisibility(View.VISIBLE);
            holder.btnUpdateStatus.setOnClickListener(v -> {
                if (onUpdateStatusClickListener != null) {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onUpdateStatusClickListener.onUpdateStatusClick(packages.get(pos));
                    }
                }
            });
        }
    }

    private int getStatusColor(int status) {
        switch (status) {
            case StatusUtil.STATUS_PICKED:
                return 0xFF2196F3; // 蓝色
            case StatusUtil.STATUS_IN_TRANSIT:
                return 0xFF9C27B0; // 紫色
            case StatusUtil.STATUS_DELIVERING:
                return 0xFF4CAF50; // 绿色
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
        TextView tvTime;
        LinearLayout layoutActions;
        MaterialButton btnCall;
        MaterialButton btnUpdateStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTrackingNo = itemView.findViewById(R.id.tvTrackingNo);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvReceiverPhone = itemView.findViewById(R.id.tvReceiverPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvItemType = itemView.findViewById(R.id.tvItemType);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvTime = itemView.findViewById(R.id.tvTime);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}