package com.expressmanagement.app.ui.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.expressmanagement.app.R;
import com.expressmanagement.app.ui.admin.fragment.DashboardFragment;
import com.expressmanagement.app.ui.admin.fragment.OrderManagementFragment;
import com.expressmanagement.app.ui.admin.fragment.SystemLogsFragment;
import com.expressmanagement.app.ui.admin.fragment.UserManagementFragment;
import com.expressmanagement.app.ui.admin.fragment.AdminProfileFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 管理员主界面
 * 包含四个Fragment：数据看板、用户管理、订单管理、系统日志
 */
public class AdminMainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;

    private DashboardFragment dashboardFragment;
    private UserManagementFragment userManagementFragment;
    private OrderManagementFragment orderManagementFragment;
    private SystemLogsFragment systemLogsFragment;

    private AdminProfileFragment adminProfileFragment;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        initViews();
        initFragments();
        setListeners();

        // 默认显示数据看板
        showFragment(dashboardFragment);
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        setSupportActionBar(toolbar);
    }

    private void initFragments() {
        dashboardFragment = new DashboardFragment();
        userManagementFragment = new UserManagementFragment();
        orderManagementFragment = new OrderManagementFragment();
        systemLogsFragment = new SystemLogsFragment();
        adminProfileFragment = new AdminProfileFragment();
    }

    private void setListeners() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                showFragment(dashboardFragment);
                toolbar.setTitle("数据看板");
                return true;
            } else if (itemId == R.id.nav_users) {
                showFragment(userManagementFragment);
                toolbar.setTitle("用户管理");
                return true;
            } else if (itemId == R.id.nav_orders) {
                showFragment(orderManagementFragment);
                toolbar.setTitle("订单管理");
                return true;
            } else if (itemId == R.id.nav_logs) {
                showFragment(systemLogsFragment);
                toolbar.setTitle("系统日志");
                return true;
            } else if (itemId == R.id.nav_admin_profile) {
                // 添加日志
                android.util.Log.d("AdminMain", "进入个人中心");
                showFragment(adminProfileFragment);
                toolbar.setTitle("我的");
                return true;
            }

            return false;
        });
    }

    private void showFragment(Fragment fragment) {
        // 🛡️ 增加判空保护
        if (fragment == null) {
            android.util.Log.e("AdminMainActivity", "尝试显示的 Fragment 为 null");
            return;
        }

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
}