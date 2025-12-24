package com.expressmanagement.app.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.ui.login.LoginActivity;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.SystemLogHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * 修改密码Activity
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private MaterialButton btnConfirm;
    
    private AppDatabase database;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        
        database = AppDatabase.getInstance(this);
        
        int userId = PreferencesUtil.getCurrentUserId(this);
        currentUser = database.userDao().getUserById(userId);
        
        initViews();
        setListeners();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnConfirm = findViewById(R.id.btnConfirm);
        
        // 设置工具栏
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setListeners() {
        btnConfirm.setOnClickListener(v -> changePassword());
    }
    
    private void changePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        // 输入验证
        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(this, "请输入原密码", Toast.LENGTH_SHORT).show();
            etOldPassword.requestFocus();
            return;
        }
        
        // 验证原密码
        if (!oldPassword.equals(currentUser.getPassword())) {
            Toast.makeText(this, "原密码错误", Toast.LENGTH_SHORT).show();
            etOldPassword.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
            etNewPassword.requestFocus();
            return;
        }
        
        if (newPassword.length() < 6) {
            Toast.makeText(this, "新密码至少6位", Toast.LENGTH_SHORT).show();
            etNewPassword.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "请确认新密码", Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return;
        }
        
        if (oldPassword.equals(newPassword)) {
            Toast.makeText(this, "新密码不能与原密码相同", Toast.LENGTH_SHORT).show();
            etNewPassword.requestFocus();
            return;
        }
        
        // 更新密码
        database.userDao().updatePassword(currentUser.getUid(), newPassword);
        
        // 记录日志
        SystemLogHelper.logPasswordChange(this, currentUser.getUid(), currentUser.getRole());
        
        // 清除登录状态
        PreferencesUtil.clearLoginStatus(this);
        
        Toast.makeText(this, "密码修改成功，请重新登录", Toast.LENGTH_LONG).show();
        
        // 跳转到登录页
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        
        finish();
    }
}