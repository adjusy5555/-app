package com.expressmanagement.app.ui.admin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.utils.TimeUtil;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * 数据看板Fragment
 */
public class DashboardFragment extends Fragment {

    private TextView tvCurrentTime;
    private TextView tvTodayOrders;
    private TextView tvTodayCompleted;
    private TextView tvTotalUsers;
    private TextView tvTotalOrders;
    private TextView tvCompletedOrders;
    private MaterialButton btnRefresh;
    private MaterialButton btnAnnouncement;

    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initDatabase();
        loadData();
        setListeners();
    }

    private void initViews(View view) {
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTodayOrders = view.findViewById(R.id.tvTodayOrders);
        tvTodayCompleted = view.findViewById(R.id.tvTodayCompleted);
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvCompletedOrders = view.findViewById(R.id.tvCompletedOrders);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnAnnouncement = view.findViewById(R.id.btnAnnouncement);
    }

    private void initDatabase() {
        db = AppDatabase.getInstance(requireContext());
    }

    private void loadData() {
        // 显示当前时间
        tvCurrentTime.setText(TimeUtil.formatDateTime(System.currentTimeMillis()));

        // 获取今天0点的时间戳
        long todayStart = TimeUtil.getTodayStartTime();

        // 今日新增订单数
        int todayCount = db.packageDao().getTodayCount(todayStart);
        tvTodayOrders.setText(String.valueOf(todayCount));

        // 今日完成订单数（今天更新为已签收状态的订单）
        // 查询状态为已签收且更新时间在今天的订单
        List<Package> allCompleted =
                db.packageDao().getPackagesByStatus(com.expressmanagement.app.utils.StatusUtil.STATUS_DELIVERED);

        int todayCompletedCount = 0;
        for (com.expressmanagement.app.entity.Package pkg : allCompleted) {
            if (pkg.getUpdateTime() >= todayStart) {
                todayCompletedCount++;
            }
        }
        tvTodayCompleted.setText(String.valueOf(todayCompletedCount));

        // 总用户数
        int totalUsers = db.userDao().getAllUsers().size();
        tvTotalUsers.setText(totalUsers + " 人");

        // 总订单数
        int totalOrders = db.packageDao().getTotalCount();
        tvTotalOrders.setText(totalOrders + " 单");

        // 已完成订单数
        int completedOrders = db.packageDao().getCompletedCount();
        tvCompletedOrders.setText(completedOrders + " 单");
    }

    private void setListeners() {
        btnRefresh.setOnClickListener(v -> {
            loadData();
            Toast.makeText(requireContext(), "数据已刷新", Toast.LENGTH_SHORT).show();
        });

        btnAnnouncement.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "发布公告功能开发中", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}