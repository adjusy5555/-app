package com.expressmanagement.app.ui.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.entity.LogisticsInfo;
import com.expressmanagement.app.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 物流轨迹适配器
 */
public class LogisticsAdapter extends RecyclerView.Adapter<LogisticsAdapter.ViewHolder> {

    private Context context;
    private List<LogisticsInfo> logisticsList = new ArrayList<>();

    public LogisticsAdapter(Context context) {
        this.context = context;
    }

    public void setLogisticsList(List<LogisticsInfo> logisticsList) {
        this.logisticsList = logisticsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_logistics, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LogisticsInfo logistics = logisticsList.get(position);
        
        // 信息
        holder.tvInfo.setText(logistics.getInfo());
        
        // 时间
        holder.tvTime.setText(TimeUtil.formatDateTime(logistics.getTimestamp()));
        
        // 第一条（最新的）高亮显示
        if (position == 0) {
            holder.tvInfo.setTextColor(0xFF4CAF50); // 绿色
            holder.tvInfo.setTextSize(15);
            holder.viewDot.setBackgroundResource(android.R.drawable.presence_online);
        } else {
            holder.tvInfo.setTextColor(0xFF333333);
            holder.tvInfo.setTextSize(14);
            holder.viewDot.setBackgroundResource(android.R.drawable.presence_invisible);
        }
        
        // 最后一条不显示连接线
        if (position == logisticsList.size() - 1) {
            holder.viewLine.setVisibility(View.INVISIBLE);
        } else {
            holder.viewLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return logisticsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewDot;
        View viewLine;
        TextView tvInfo;
        TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewDot = itemView.findViewById(R.id.viewDot);
            viewLine = itemView.findViewById(R.id.viewLine);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}