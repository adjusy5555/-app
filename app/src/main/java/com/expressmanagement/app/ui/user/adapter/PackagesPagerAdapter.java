package com.expressmanagement.app.ui.user.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.expressmanagement.app.ui.user.fragment.PackageListFragment;

/**
 * 快递列表ViewPager适配器
 * 用于"我寄出的"和"我收到的"两个Tab切换
 */
public class PackagesPagerAdapter extends FragmentStateAdapter {

    public PackagesPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            // 我寄出的
            return PackageListFragment.newInstance(PackageListFragment.TYPE_SENT);
        } else {
            // 我收到的
            return PackageListFragment.newInstance(PackageListFragment.TYPE_RECEIVED);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 两个Tab
    }
}