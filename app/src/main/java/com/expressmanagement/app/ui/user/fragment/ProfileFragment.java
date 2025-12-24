package com.expressmanagement.app.ui.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.User;
import com.expressmanagement.app.ui.login.LoginActivity;
import com.expressmanagement.app.ui.user.AddressListActivity;
import com.expressmanagement.app.ui.user.ChangePasswordActivity;
import com.expressmanagement.app.ui.user.EditProfileActivity;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.google.android.material.button.MaterialButton;

/**
 * 个人中心Fragment
 */
public class ProfileFragment extends Fragment {

    private TextView tvNickname;
    private TextView tvPhone;
    private LinearLayout layoutAddress;
    private LinearLayout layoutPassword;
    private LinearLayout layoutProfile;
    private MaterialButton btnLogout;
    
    private AppDatabase database;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        database = AppDatabase.getInstance(requireContext());
        
        initViews(view);
        loadUserInfo();
        setListeners();
    }
    
    private void initViews(View view) {
        tvNickname = view.findViewById(R.id.tvNickname);
        tvPhone = view.findViewById(R.id.tvPhone);
        layoutAddress = view.findViewById(R.id.layoutAddress);
        layoutPassword = view.findViewById(R.id.layoutPassword);
        layoutProfile = view.findViewById(R.id.layoutProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
    }
    
    private void loadUserInfo() {
        int userId = PreferencesUtil.getCurrentUserId(requireContext());
        currentUser = database.userDao().getUserById(userId);
        
        if (currentUser != null) {
            String nickname = currentUser.getNickname();
            if (TextUtils.isEmpty(nickname)) {
                nickname = currentUser.getUsername();
            }
            tvNickname.setText(nickname);
            
            // 手机号脱敏显示
            String phone = currentUser.getPhone();
            if (phone != null && phone.length() == 11) {
                phone = phone.substring(0, 3) + "****" + phone.substring(7);
            }
            tvPhone.setText(phone);
        }
    }
    
    private void setListeners() {
        // 地址管理
        layoutAddress.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddressListActivity.class);
            startActivity(intent);
        });
        
        // 修改密码
        layoutPassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        
        // 个人资料
        layoutProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            startActivity(intent);
        });
        
        // 退出登录
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> logout())
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void logout() {
        // 清除登录状态
        PreferencesUtil.clearLoginStatus(requireContext());
        
        Toast.makeText(requireContext(), "已退出登录", Toast.LENGTH_SHORT).show();
        
        // 跳转到登录页
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        
        requireActivity().finish();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 每次显示时刷新用户信息
        loadUserInfo();
    }
}