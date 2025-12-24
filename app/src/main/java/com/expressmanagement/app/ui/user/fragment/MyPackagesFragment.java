package com.expressmanagement.app.ui.user.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.expressmanagement.app.R;
import com.expressmanagement.app.ui.user.adapter.PackagesPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * 我的快递Fragment
 * 包含两个Tab：我寄出的、我收到的
 */
public class MyPackagesFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private PackagesPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_packages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewPager();
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
    }

    private void setupViewPager() {
        // 创建适配器
        pagerAdapter = new PackagesPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 关联TabLayout和ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("我寄出的");
                    } else {
                        tab.setText("我收到的");
                    }
                }).attach();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 刷新数据
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();
        }
    }
}