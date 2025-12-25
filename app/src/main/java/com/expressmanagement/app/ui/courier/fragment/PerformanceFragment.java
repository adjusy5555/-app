package com.expressmanagement.app.ui.courier.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.ui.courier.HistoryActivity;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.TimeUtil;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

/**
 * 历史业绩Fragment
 */
public class PerformanceFragment extends Fragment {

    private TextView tvTotalCount;
    private TextView tvTodayDelivered;
    private TextView tvWeekDelivered;
    private TextView tvMonthDelivered;
    private MaterialButton btnViewHistory;
    
    private AppDatabase database;
    private int currentCourierId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_performance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        database = AppDatabase.getInstance(requireContext());
        currentCourierId = PreferencesUtil.getCurrentUserId(requireContext());
        
        initViews(view);
        loadStatistics();
        setListeners();
    }
    
    private void initViews(View view) {
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        tvTodayDelivered = view.findViewById(R.id.tvTodayDelivered);
        tvWeekDelivered = view.findViewById(R.id.tvWeekDelivered);
        tvMonthDelivered = view.findViewById(R.id.tvMonthDelivered);
        btnViewHistory = view.findViewById(R.id.btnViewHistory);
    }
    
    private void loadStatistics() {
        // 累计完成订单数
        int totalCount = database.packageDao().getCourierCompletedCount(currentCourierId);
        tvTotalCount.setText(String.valueOf(totalCount));
        
        // 今日完成
        long todayStart = TimeUtil.getTodayStartTime();
        int todayCount = database.packageDao().getCourierCompletedCountByTime(
                currentCourierId, todayStart);
        tvTodayDelivered.setText(todayCount + " 单");
        
        // 本周完成
        long weekStart = getWeekStartTime();
        int weekCount = database.packageDao().getCourierCompletedCountByTime(
                currentCourierId, weekStart);
        tvWeekDelivered.setText(weekCount + " 单");
        
        // 本月完成
        long monthStart = getMonthStartTime();
        int monthCount = database.packageDao().getCourierCompletedCountByTime(
                currentCourierId, monthStart);
        tvMonthDelivered.setText(monthCount + " 单");
    }
    
    private void setListeners() {
        btnViewHistory.setOnClickListener(v -> {
            // 跳转到历史记录页面
            Intent intent = new Intent(requireContext(), HistoryActivity.class);
            startActivity(intent);
        });
    }
    
    /**
     * 获取本周一0点的时间戳
     */
    private long getWeekStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    
    /**
     * 获取本月1日0点的时间戳
     */
    private long getMonthStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    
    /**
     * 刷新数据（供Activity调用）
     */
    public void refreshData() {
        if (isAdded() && getView() != null) {
            loadStatistics();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }
}