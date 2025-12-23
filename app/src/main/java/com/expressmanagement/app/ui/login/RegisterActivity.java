package com.expressmanagement.app.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.utils.SystemLogHelper;
import com.google.android.material.button.MaterialButton;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RadioGroup rgRole;
    private RadioButton rbUser;
    private RadioButton rbCourier;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etPhone;
    private EditText etNickname;
    private MaterialButton btnRegister;
    private TextView tvGoLogin;
    
    private AppDatabase database;
    
    // 正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,16}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // 初始化数据库
        database = AppDatabase.getInstance(this);
        
        // 初始化视图
        initViews();
        
        // 设置监听器
        setListeners();
    }
    
    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        rgRole = findViewById(R.id.rgRole);
        rbUser = findViewById(R.id.rbUser);
        rbCourier = findViewById(R.id.rbCourier);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etNickname = findViewById(R.id.etNickname);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoLogin = findViewById(R.id.tvGoLogin);
    }
    
    private void setListeners() {
        // 返回按钮
        ivBack.setOnClickListener(v -> finish());
        
        // 注册按钮
        btnRegister.setOnClickListener(v -> performRegister());
        
        // 跳转登录页面
        tvGoLogin.setOnClickListener(v -> finish());
    }
    
    private void performRegister() {
        // 获取输入内容
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();
        int role = rbUser.isChecked() ? 0 : 1; // 0-普通用户, 1-快递员
        
        // 输入验证
        if (!validateInput(username, password, confirmPassword, phone)) {
            return;
        }
        
        // 检查账号是否已存在
        if (database.userDao().checkUsernameExists(username) > 0) {
            Toast.makeText(this, "该账号已被注册", Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return;
        }
        
        // 检查手机号是否已被使用
        if (database.userDao().checkPhoneExists(phone) > 0) {
            Toast.makeText(this, "该手机号已被注册", Toast.LENGTH_SHORT).show();
            etPhone.requestFocus();
            return;
        }
        
        // 创建用户对象
        User user = new User(username, password, phone, role);
        if (!TextUtils.isEmpty(nickname)) {
            user.setNickname(nickname);
        } else {
            user.setNickname(username); // 默认昵称为账号
        }
        
        // 插入数据库
        long userId = database.userDao().insert(user);
        
        if (userId > 0) {
            // 注册成功
            String roleStr = role == 0 ? "普通用户" : "快递员";
            Toast.makeText(this, "注册成功！您的身份是：" + roleStr, Toast.LENGTH_LONG).show();
            
            // 记录注册日志
            SystemLogHelper.logRegister(this, (int) userId, role);
            
            // 返回登录页
            finish();
        } else {
            Toast.makeText(this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean validateInput(String username, String password, 
                                  String confirmPassword, String phone) {
        // 验证账号
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return false;
        }
        
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            Toast.makeText(this, "账号格式不正确（4-16位字母数字下划线）", Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return false;
        }
        
        // 验证密码
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return false;
        }
        
        if (password.length() < 6) {
            Toast.makeText(this, "密码至少6位", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return false;
        }
        
        // 验证确认密码
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return false;
        }
        
        // 验证手机号
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            etPhone.requestFocus();
            return false;
        }
        
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
            etPhone.requestFocus();
            return false;
        }
        
        return true;
    }
}