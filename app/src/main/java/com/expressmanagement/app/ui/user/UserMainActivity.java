package com.expressmanagement.app.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.ui.login.LoginActivity;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.google.android.material.button.MaterialButton;

public class UserMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 临时简单布局
        setContentView(createTempLayout());

        // 显示用户信息
        int userId = PreferencesUtil.getCurrentUserId(this);
        User user = AppDatabase.getInstance(this).userDao().getUserById(userId);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("欢迎进入用户主界面\n\n" +
                "账号：" + user.getUsername() + "\n" +
                "角色：普通用户");

        // 退出登录按钮
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        PreferencesUtil.clearLoginStatus(this);
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private int createTempLayout() {
        // 临时返回一个布局ID（需要创建activity_user_main_temp.xml）
        return R.layout.activity_user_main_temp;
    }
}