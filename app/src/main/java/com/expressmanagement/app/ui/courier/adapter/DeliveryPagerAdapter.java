package com.expressmanagement.app.ui.courier.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.expressmanagement.app.ui.courier.fragment.DeliveryListFragment;
import com.expressmanagement.app.utils.StatusUtil;

/**
 * 配送管理ViewPager适配器
 * 用于"已揽件"、"运输中"、"派送中"三个Tab切换
 */
public class DeliveryPagerAdapter extends FragmentStateAdapter {

    private int courierId;

    public DeliveryPagerAdapter(@NonNull Fragment fragment, int courierId) {
        super(fragment);
        this.courierId = courierId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // 已揽件
                return DeliveryListFragment.newInstance(StatusUtil.STATUS_PICKED, courierId);
            case 1:
                // 运输中
                return DeliveryListFragment.newInstance(StatusUtil.STATUS_IN_TRANSIT, courierId);
            case 2:
                // 派送中
                return DeliveryListFragment.newInstance(StatusUtil.STATUS_DELIVERING, courierId);
            default:
                return DeliveryListFragment.newInstance(StatusUtil.STATUS_PICKED, courierId);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 三个Tab
    }
}