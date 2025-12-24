package com.expressmanagement.app.ui.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Address;
import com.expressmanagement.app.entity.LogisticsInfo;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.ui.user.AddressListActivity;
import com.expressmanagement.app.utils.PriceCalculator;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.SystemLogHelper;
import com.expressmanagement.app.utils.TrackingNoUtil;
import com.google.android.material.button.MaterialButton;

/**
 * 寄件Fragment
 */
public class SendPackageFragment extends Fragment {

    private TextView tvSelectAddress;
    private LinearLayout layoutAddressInfo;
    private TextView tvNoAddress;
    private TextView tvReceiverName;
    private TextView tvReceiverPhone;
    private TextView tvAddress;
    
    private Spinner spinnerItemType;
    private EditText etWeight;
    private EditText etRemark;
    private TextView tvPrice;
    private MaterialButton btnSubmit;
    
    private AppDatabase database;
    private Address selectedAddress;
    private int currentUserId;
    
    private static final int REQUEST_SELECT_ADDRESS = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_package, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化
        database = AppDatabase.getInstance(requireContext());
        currentUserId = PreferencesUtil.getCurrentUserId(requireContext());
        
        // 初始化视图
        initViews(view);
        
        // 初始化物品类型下拉框
        initItemTypeSpinner();
        
        // 设置监听器
        setListeners();
        
        // 加载默认地址
        loadDefaultAddress();
    }
    
    private void initViews(View view) {
        tvSelectAddress = view.findViewById(R.id.tvSelectAddress);
        layoutAddressInfo = view.findViewById(R.id.layoutAddressInfo);
        tvNoAddress = view.findViewById(R.id.tvNoAddress);
        tvReceiverName = view.findViewById(R.id.tvReceiverName);
        tvReceiverPhone = view.findViewById(R.id.tvReceiverPhone);
        tvAddress = view.findViewById(R.id.tvAddress);
        
        spinnerItemType = view.findViewById(R.id.spinnerItemType);
        etWeight = view.findViewById(R.id.etWeight);
        etRemark = view.findViewById(R.id.etRemark);
        tvPrice = view.findViewById(R.id.tvPrice);
        btnSubmit = view.findViewById(R.id.btnSubmit);
    }
    
    private void initItemTypeSpinner() {
        String[] itemTypes = PriceCalculator.getItemTypes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, itemTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemType.setAdapter(adapter);
    }
    
    private void setListeners() {
        // 选择地址
        tvSelectAddress.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddressListActivity.class);
            intent.putExtra("select_mode", true);
            startActivityForResult(intent, REQUEST_SELECT_ADDRESS);
        });
        
        // 重量输入监听，自动计算价格
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculatePrice();
            }
        });
        
        // 提交按钮
        btnSubmit.setOnClickListener(v -> submitPackage());
    }
    
    private void loadDefaultAddress() {
        Address defaultAddress = database.addressDao().getDefaultAddress(currentUserId);
        if (defaultAddress != null) {
            updateAddressDisplay(defaultAddress);
        }
    }
    
    private void updateAddressDisplay(Address address) {
        selectedAddress = address;
        
        if (address == null) {
            layoutAddressInfo.setVisibility(View.GONE);
            tvNoAddress.setVisibility(View.VISIBLE);
        } else {
            layoutAddressInfo.setVisibility(View.VISIBLE);
            tvNoAddress.setVisibility(View.GONE);
            
            tvReceiverName.setText(address.getReceiverName());
            tvReceiverPhone.setText(address.getReceiverPhone());
            tvAddress.setText(address.getFullAddress());
        }
    }
    
    private void calculatePrice() {
        String weightStr = etWeight.getText().toString().trim();
        if (TextUtils.isEmpty(weightStr)) {
            tvPrice.setText("¥0.00");
            return;
        }
        
        try {
            float weight = Float.parseFloat(weightStr);
            String itemType = spinnerItemType.getSelectedItem().toString();
            float price = PriceCalculator.calculatePrice(weight, itemType);
            tvPrice.setText("¥" + PriceCalculator.formatPrice(price));
        } catch (NumberFormatException e) {
            tvPrice.setText("¥0.00");
        }
    }
    
    private void submitPackage() {
        // 验证地址
        if (selectedAddress == null) {
            Toast.makeText(requireContext(), "请选择收货地址", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证重量
        String weightStr = etWeight.getText().toString().trim();
        if (TextUtils.isEmpty(weightStr)) {
            Toast.makeText(requireContext(), "请输入重量", Toast.LENGTH_SHORT).show();
            etWeight.requestFocus();
            return;
        }
        
        float weight;
        try {
            weight = Float.parseFloat(weightStr);
            if (weight <= 0) {
                Toast.makeText(requireContext(), "重量必须大于0", Toast.LENGTH_SHORT).show();
                etWeight.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "重量格式不正确", Toast.LENGTH_SHORT).show();
            etWeight.requestFocus();
            return;
        }
        
        // 获取物品类型
        String itemType = spinnerItemType.getSelectedItem().toString();
        
        // 计算价格
        float price = PriceCalculator.calculatePrice(weight, itemType);
        
        // 生成运单号
        String trackingNo = TrackingNoUtil.generateTrackingNo();
        
        // 创建订单
        Package pkg = new Package();
        pkg.setTrackingNo(trackingNo);
        pkg.setSenderId(currentUserId);
        pkg.setReceiverName(selectedAddress.getReceiverName());
        pkg.setReceiverPhone(selectedAddress.getReceiverPhone());
        pkg.setAddressId(selectedAddress.getAid());
        pkg.setItemType(itemType);
        pkg.setWeight(weight);
        pkg.setPrice(price);
        pkg.setStatus(0); // 待揽件
        
        String remark = etRemark.getText().toString().trim();
        if (!TextUtils.isEmpty(remark)) {
            pkg.setRemark(remark);
        }
        
        // 插入数据库
        long packageId = database.packageDao().insert(pkg);
        
        if (packageId > 0) {
            // 添加物流轨迹
            LogisticsInfo logistics = new LogisticsInfo((int) packageId, 
                    "订单已创建，等待快递员揽件");
            database.logisticsInfoDao().insert(logistics);
            
            // 记录日志
            SystemLogHelper.logCreatePackage(requireContext(), currentUserId, 
                    (int) packageId, trackingNo);
            
            // 提示成功
            Toast.makeText(requireContext(), 
                    "寄件成功！运单号：" + trackingNo, Toast.LENGTH_LONG).show();
            
            // 清空表单
            clearForm();
        } else {
            Toast.makeText(requireContext(), "寄件失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void clearForm() {
        etWeight.setText("");
        etRemark.setText("");
        spinnerItemType.setSelection(0);
        tvPrice.setText("¥0.00");
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_SELECT_ADDRESS && resultCode == -1 && data != null) {
            int addressId = data.getIntExtra("address_id", -1);
            if (addressId != -1) {
                Address address = database.addressDao().getAddressById(addressId);
                updateAddressDisplay(address);
            }
        }
    }
}