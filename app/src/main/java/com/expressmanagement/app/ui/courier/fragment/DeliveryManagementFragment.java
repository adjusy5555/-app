package com.expressmanagement.app.ui.courier.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.ui.courier.adapter.DeliveryPagerAdapter;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.StatusUtil;
import com.expressmanagement.app.utils.TimeUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * 配送管理Fragment
 * 包含三个Tab：已揽件、运输中、派送中
 */
public class DeliveryManagementFragment extends Fragment {

    private TextView tvPendingCount;
    private TextView tvDeliveringCount;
    private TextView tvTodayCount;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private DeliveryPagerAdapter pagerAdapter;
    private AppDatabase database;
    private int currentCourierId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delivery_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getInstance(requireContext());
        currentCourierId = PreferencesUtil.getCurrentUserId(requireContext());

        initViews(view);
        loadStatistics();
        setupViewPager();
    }

    private void initViews(View view) {
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvDeliveringCount = view.findViewById(R.id.tvDeliveringCount);
        tvTodayCount = view.findViewById(R.id.tvTodayCount);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
    }

    private void loadStatistics() {
        // 待配送数量（已揽件 + 运输中）
        int pickedCount = database.packageDao().getCourierPackagesByStatus(
                currentCourierId, StatusUtil.STATUS_PICKED).size();
        int transitCount = database.packageDao().getCourierPackagesByStatus(
                currentCourierId, StatusUtil.STATUS_IN_TRANSIT).size();
        tvPendingCount.setText(String.valueOf(pickedCount + transitCount));

        // 配送中数量
        int deliveringCount = database.packageDao().getCourierPackagesByStatus(
                currentCourierId, StatusUtil.STATUS_DELIVERING).size();
        tvDeliveringCount.setText(String.valueOf(deliveringCount));

        // 今日完成数量
        long todayStart = TimeUtil.getTodayStartTime();
        int todayCount = database.packageDao().getCourierCompletedCountByTime(
                currentCourierId, todayStart);
        tvTodayCount.setText(String.valueOf(todayCount));
    }

    private void setupViewPager() {
        // 创建适配器
        pagerAdapter = new DeliveryPagerAdapter(this, currentCourierId);
        viewPager.setAdapter(pagerAdapter);

        // 设置离屏页面数，避免Fragment被销毁
        viewPager.setOffscreenPageLimit(3);

        // 关联TabLayout和ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("已揽件");
                            break;
                        case 1:
                            tab.setText("运输中");
                            break;
                        case 2:
                            tab.setText("派送中");
                            break;
                    }
                }).attach();
    }

    /**
     * 刷新数据（供Activity调用）
     */
    public void refreshData() {
        if (isAdded() && getView() != null) {
            loadStatistics();
            if (pagerAdapter != null) {
                pagerAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }
}