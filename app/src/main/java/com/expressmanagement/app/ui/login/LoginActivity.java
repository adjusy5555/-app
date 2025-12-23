package com.expressmanagement.app.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.ui.admin.AdminMainActivity;
import com.expressmanagement.app.ui.courier.CourierMainActivity;
import com.expressmanagement.app.ui.user.UserMainActivity;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.SystemLogHelper;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private CheckBox cbRememberPassword;
    private MaterialButton btnLogin;
    private TextView tvGoRegister;
    
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // 初始化数据库
        database = AppDatabase.getInstance(this);
        
        // 检查是否已登录
        if (PreferencesUtil.isLoggedIn(this)) {
            jumpToMainActivity();
            return;
        }
        
        // 初始化视图
        initViews();
        
        // 加载保存的账号密码
        loadSavedAccount();
        
        // 设置监听器
        setListeners();
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberPassword = findViewById(R.id.cbRememberPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);
    }
    
    private void loadSavedAccount() {
        boolean rememberPassword = PreferencesUtil.isRememberPassword(this);
        if (rememberPassword) {
            String username = PreferencesUtil.getSavedUsername(this);
            String password = PreferencesUtil.getSavedPassword(this);
            etUsername.setText(username);
            etPassword.setText(password);
            cbRememberPassword.setChecked(true);
        }
    }
    
    private void setListeners() {
        // 登录按钮点击事件
        btnLogin.setOnClickListener(v -> performLogin());
        
        // 跳转注册页面
        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    
    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        // 输入验证
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }
        
        // 查询数据库验证账号密码
        User user = database.userDao().login(username, password);
        
        if (user == null) {
            Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 登录成功
        onLoginSuccess(user, username, password);
    }
    
    private void onLoginSuccess(User user, String username, String password) {
        // 保存登录状态
        PreferencesUtil.saveCurrentUser(this, user.getUid(), user.getRole());
        
        // 保存记住密码
        boolean remember = cbRememberPassword.isChecked();
        PreferencesUtil.saveLoginInfo(this, username, password, remember);
        
        // 记录登录日志
        SystemLogHelper.logLogin(this, user.getUid(), user.getRole());
        
        // 显示欢迎信息
        String nickname = user.getNickname();
        String welcomeMsg = "欢迎" + user.getRoleName() + "：" + 
                          (TextUtils.isEmpty(nickname) ? username : nickname);
        Toast.makeText(this, welcomeMsg, Toast.LENGTH_SHORT).show();
        
        // 跳转到对应的主界面
        jumpToMainActivity();
    }
    
    private void jumpToMainActivity() {
        int role = PreferencesUtil.getCurrentUserRole(this);
        Intent intent;
        
        switch (role) {
            case 0: // 普通用户
                intent = new Intent(this, UserMainActivity.class);
                break;
            case 1: // 快递员
                intent = new Intent(this, CourierMainActivity.class);
                break;
            case 2: // 管理员
                intent = new Intent(this, AdminMainActivity.class);
                break;
            default:
                Toast.makeText(this, "用户角色异常", Toast.LENGTH_SHORT).show();
                return;
        }
        
        startActivity(intent);
        finish(); // 结束登录页面，防止返回
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 从注册页返回时，清空密码框（安全考虑）
        if (!cbRememberPassword.isChecked()) {
            etPassword.setText("");
        }
    }
}