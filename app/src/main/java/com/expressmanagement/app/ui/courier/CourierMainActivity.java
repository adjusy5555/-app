package com.expressmanagement.app.ui.courier;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.expressmanagement.app.R;
import com.expressmanagement.app.ui.courier.fragment.CourierProfileFragment;
import com.expressmanagement.app.ui.courier.fragment.DeliveryManagementFragment;
import com.expressmanagement.app.ui.courier.fragment.PerformanceFragment;
import com.expressmanagement.app.ui.courier.fragment.TaskHallFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 快递员主界面
 * 包含底部导航栏，切换四个Fragment：任务大厅、配送管理、历史业绩、个人中心
 */
public class CourierMainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;

    private TaskHallFragment taskHallFragment;
    private DeliveryManagementFragment deliveryManagementFragment;
    private PerformanceFragment performanceFragment;
    private CourierProfileFragment courierProfileFragment;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_main);

        initViews();
        initFragments();
        setListeners();

        // 默认显示任务大厅
        showFragment(taskHallFragment);
        bottomNavigation.setSelectedItemId(R.id.nav_task_hall);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        setSupportActionBar(toolbar);
    }

    private void initFragments() {
        taskHallFragment = new TaskHallFragment();
        deliveryManagementFragment = new DeliveryManagementFragment();
        performanceFragment = new PerformanceFragment();
        courierProfileFragment = new CourierProfileFragment();
    }

    private void setListeners() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_task_hall) {
                showFragment(taskHallFragment);
                toolbar.setTitle("任务大厅");
                return true;
            } else if (itemId == R.id.nav_delivery) {
                showFragment(deliveryManagementFragment);
                toolbar.setTitle("配送管理");
                return true;
            } else if (itemId == R.id.nav_performance) {
                showFragment(performanceFragment);
                toolbar.setTitle("历史业绩");
                return true;
            } else if (itemId == R.id.nav_courier_profile) {
                showFragment(courierProfileFragment);
                toolbar.setTitle("个人中心");
                return true;
            }

            return false;
        });
    }

    private void showFragment(Fragment fragment) {
        if (currentFragment == fragment) {
            return;
        }

        // 使用add和hide/show机制，避免重复创建Fragment
        if (fragment.isAdded()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(currentFragment)
                    .show(fragment)
                    .commit();
        } else {
            if (currentFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(currentFragment)
                        .add(R.id.fragmentContainer, fragment)
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentContainer, fragment)
                        .commit();
            }
        }

        currentFragment = fragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新当前Fragment数据
        if (currentFragment instanceof TaskHallFragment) {
            ((TaskHallFragment) currentFragment).refreshData();
        } else if (currentFragment instanceof DeliveryManagementFragment) {
            ((DeliveryManagementFragment) currentFragment).refreshData();
        } else if (currentFragment instanceof PerformanceFragment) {
            ((PerformanceFragment) currentFragment).refreshData();
        }
    }
}