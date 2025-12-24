package com.expressmanagement.app.ui.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.expressmanagement.app.R;
import com.expressmanagement.app.ui.user.fragment.MyPackagesFragment;
import com.expressmanagement.app.ui.user.fragment.ProfileFragment;
import com.expressmanagement.app.ui.user.fragment.SendPackageFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 用户主界面
 * 包含底部导航栏，切换三个Fragment：寄件、我的快递、个人中心
 */
public class UserMainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;

    private SendPackageFragment sendPackageFragment;
    private MyPackagesFragment myPackagesFragment;
    private ProfileFragment profileFragment;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        // 初始化视图
        initViews();

        // 初始化Fragment
        initFragments();

        // 设置监听器
        setListeners();

        // 默认显示寄件页面
        showFragment(sendPackageFragment);
        bottomNavigation.setSelectedItemId(R.id.nav_send);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // 设置工具栏
        setSupportActionBar(toolbar);
    }

    private void initFragments() {
        sendPackageFragment = new SendPackageFragment();
        myPackagesFragment = new MyPackagesFragment();
        profileFragment = new ProfileFragment();
    }

    private void setListeners() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_send) {
                showFragment(sendPackageFragment);
                toolbar.setTitle("寄件");
                return true;
            } else if (itemId == R.id.nav_packages) {
                showFragment(myPackagesFragment);
                toolbar.setTitle("我的快递");
                return true;
            } else if (itemId == R.id.nav_profile) {
                showFragment(profileFragment);
                toolbar.setTitle("个人中心");
                return true;
            }

            return false;
        });
    }

    /**
     * 显示指定的Fragment
     */
    private void showFragment(Fragment fragment) {
        if (currentFragment == fragment) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        currentFragment = fragment;
    }
}