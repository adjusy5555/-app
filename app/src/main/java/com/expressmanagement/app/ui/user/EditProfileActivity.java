package com.expressmanagement.app.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.regex.Pattern;

/**
 * 编辑个人资料Activity
 */
public class EditProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private EditText etUsername;
    private EditText etNickname;
    private EditText etPhone;
    private EditText etRole;
    private MaterialButton btnSave;
    
    private AppDatabase database;
    private User currentUser;
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        
        database = AppDatabase.getInstance(this);
        
        int userId = PreferencesUtil.getCurrentUserId(this);
        currentUser = database.userDao().getUserById(userId);
        
        initViews();
        loadUserData();
        setListeners();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etUsername = findViewById(R.id.etUsername);
        etNickname = findViewById(R.id.etNickname);
        etPhone = findViewById(R.id.etPhone);
        etRole = findViewById(R.id.etRole);
        btnSave = findViewById(R.id.btnSave);
        
        // 设置工具栏
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void loadUserData() {
        if (currentUser != null) {
            etUsername.setText(currentUser.getUsername());
            
            String nickname = currentUser.getNickname();
            if (!TextUtils.isEmpty(nickname)) {
                etNickname.setText(nickname);
            }
            
            etPhone.setText(currentUser.getPhone());
            etRole.setText(currentUser.getRoleName());
        }
    }
    
    private void setListeners() {
        btnSave.setOnClickListener(v -> saveProfile());
    }
    
    private void saveProfile() {
        String nickname = etNickname.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        
        // 输入验证
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            etPhone.requestFocus();
            return;
        }
        
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
            etPhone.requestFocus();
            return;
        }
        
        // 检查手机号是否被其他用户使用
        User existUser = database.userDao().getUserByPhone(phone);
        if (existUser != null && existUser.getUid() != currentUser.getUid()) {
            Toast.makeText(this, "该手机号已被其他用户使用", Toast.LENGTH_SHORT).show();
            etPhone.requestFocus();
            return;
        }
        
        // 更新用户信息
        currentUser.setNickname(TextUtils.isEmpty(nickname) ? currentUser.getUsername() : nickname);
        currentUser.setPhone(phone);
        
        database.userDao().update(currentUser);
        
        Toast.makeText(this, "资料已更新", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}