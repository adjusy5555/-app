package com.expressmanagement.app.ui.user.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.entity.Address;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 地址列表适配器
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private List<Address> addresses = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private boolean selectMode; // 选择模式

    public interface OnItemClickListener {
        void onItemClick(Address address);
        void onEditClick(Address address);
        void onDeleteClick(Address address);
        void onSetDefaultClick(Address address);
    }

    public AddressAdapter(Context context, boolean selectMode) {
        this.context = context;
        this.selectMode = selectMode;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 使用holder.getAdapterPosition()获取实时位置
        final int currentPosition = holder.getAdapterPosition();
        if (currentPosition == RecyclerView.NO_POSITION) {
            return;
        }

        Address address = addresses.get(currentPosition);

        // 标签
        if (!TextUtils.isEmpty(address.getTag())) {
            holder.tvTag.setText(address.getTag());
            holder.tvTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvTag.setVisibility(View.GONE);
        }

        // 默认地址标识
        if (address.isDefault()) {
            holder.tvDefault.setVisibility(View.VISIBLE);
            holder.btnSetDefault.setVisibility(View.GONE);
        } else {
            holder.tvDefault.setVisibility(View.GONE);
            holder.btnSetDefault.setVisibility(View.VISIBLE);
        }

        // 收件人信息
        holder.tvReceiverName.setText(address.getReceiverName());

        // 手机号脱敏
        String phone = address.getReceiverPhone();
        if (phone != null && phone.length() == 11) {
            phone = phone.substring(0, 3) + "****" + phone.substring(7);
        }
        holder.tvReceiverPhone.setText(phone);

        // 详细地址
        holder.tvAddress.setText(address.getFullAddress());

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(addresses.get(pos));
                }
            }
        });

        // 编辑按钮
        holder.ivEdit.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClickListener.onEditClick(addresses.get(pos));
                }
            }
        });

        // 删除按钮
        holder.ivDelete.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClickListener.onDeleteClick(addresses.get(pos));
                }
            }
        });

        // 设为默认按钮
        holder.btnSetDefault.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClickListener.onSetDefaultClick(addresses.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag;
        TextView tvDefault;
        TextView tvReceiverName;
        TextView tvReceiverPhone;
        TextView tvAddress;
        ImageView ivEdit;
        ImageView ivDelete;
        MaterialButton btnSetDefault;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvTag);
            tvDefault = itemView.findViewById(R.id.tvDefault);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvReceiverPhone = itemView.findViewById(R.id.tvReceiverPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            btnSetDefault = itemView.findViewById(R.id.btnSetDefault);
        }
    }
}