package com.expressmanagement.app.ui.admin;

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

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_admin_main_temp);
        
        int userId = PreferencesUtil.getCurrentUserId(this);
        User user = AppDatabase.getInstance(this).userDao().getUserById(userId);
        
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("欢迎进入管理员主界面\n\n" + 
                         "账号：" + user.getUsername() + "\n" +
                         "角色：管理员\n\n" +
                         "可以使用默认账号登录：\nadmin / admin123");
        
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
}