package com.expressmanagement.app.ui.courier.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Address;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.utils.PriceCalculator;
import com.expressmanagement.app.utils.TimeUtil;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务列表适配器
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context;
    private List<Package> packages = new ArrayList<>();
    private OnPickupClickListener onPickupClickListener;
    private AppDatabase database;

    public interface OnPickupClickListener {
        void onPickupClick(Package pkg);
    }

    public TaskAdapter(Context context) {
        this.context = context;
        this.database = AppDatabase.getInstance(context);
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
        notifyDataSetChanged();
    }

    public void setOnPickupClickListener(OnPickupClickListener listener) {
        this.onPickupClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Package pkg = packages.get(position);
        
        // 运单号
        holder.tvTrackingNo.setText(pkg.getTrackingNo());
        
        // 时间（显示相对时间）
        long timeAgo = System.currentTimeMillis() - pkg.getCreateTime();
        holder.tvTime.setText(getTimeAgoText(timeAgo));
        
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
        
        // 接单按钮
        holder.btnPickup.setOnClickListener(v -> {
            if (onPickupClickListener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onPickupClickListener.onPickupClick(packages.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    private String getTimeAgoText(long timeAgo) {
        long seconds = timeAgo / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "天前";
        } else if (hours > 0) {
            return hours + "小时前";
        } else if (minutes > 0) {
            return minutes + "分钟前";
        } else {
            return "刚刚";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrackingNo;
        TextView tvTime;
        TextView tvReceiverName;
        TextView tvReceiverPhone;
        TextView tvAddress;
        TextView tvItemType;
        TextView tvWeight;
        TextView tvPrice;
        MaterialButton btnPickup;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrackingNo = itemView.findViewById(R.id.tvTrackingNo);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvReceiverPhone = itemView.findViewById(R.id.tvReceiverPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvItemType = itemView.findViewById(R.id.tvItemType);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnPickup = itemView.findViewById(R.id.btnPickup);
        }
    }
}