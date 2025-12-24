package com.expressmanagement.app.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Address;
import com.expressmanagement.app.utils.PreferencesUtil;
import com.expressmanagement.app.utils.SystemLogHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.regex.Pattern;

/**
 * 添加/编辑地址Activity
 */
public class EditAddressActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private EditText etReceiverName;
    private EditText etReceiverPhone;
    private Spinner spinnerProvince;
    private Spinner spinnerCity;
    private Spinner spinnerDistrict;
    private EditText etDetailAddress;
    private RadioGroup rgTag;
    private RadioButton rbHome;
    private RadioButton rbCompany;
    private RadioButton rbSchool;
    private CheckBox cbDefault;
    private MaterialButton btnSave;
    
    private AppDatabase database;
    private int currentUserId;
    private int addressId = -1; // -1表示新增，否则为编辑
    private Address editingAddress;
    
    // 简化的省市区数据
    private String[] provinces = {"广东省", "北京市", "上海市", "浙江省", "江苏省"};
    private String[][] cities = {
        {"广州市", "深圳市", "东莞市", "佛山市"},
        {"北京市"},
        {"上海市"},
        {"杭州市", "宁波市", "温州市"},
        {"南京市", "苏州市", "无锡市"}
    };
    private String[][][] districts = {
        {
            {"天河区", "越秀区", "海珠区"},
            {"南山区", "福田区", "罗湖区"},
            {"东城区", "南城区", "万江区"},
            {"禅城区", "南海区", "顺德区"}
        },
        {
            {"东城区", "西城区", "朝阳区", "海淀区"}
        },
        {
            {"黄浦区", "徐汇区", "长宁区", "静安区"}
        },
        {
            {"西湖区", "拱墅区", "滨江区"},
            {"海曙区", "江北区", "鄞州区"},
            {"鹿城区", "龙湾区", "瓯海区"}
        },
        {
            {"玄武区", "秦淮区", "建邺区"},
            {"姑苏区", "吴中区", "相城区"},
            {"梁溪区", "滨湖区", "新吴区"}
        }
    };
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        
        database = AppDatabase.getInstance(this);
        currentUserId = PreferencesUtil.getCurrentUserId(this);
        
        // 获取地址ID（编辑模式）
        addressId = getIntent().getIntExtra("address_id", -1);
        
        initViews();
        setupSpinners();
        setListeners();
        
        // 如果是编辑模式，加载地址数据
        if (addressId != -1) {
            loadAddressData();
        }
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etReceiverName = findViewById(R.id.etReceiverName);
        etReceiverPhone = findViewById(R.id.etReceiverPhone);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        etDetailAddress = findViewById(R.id.etDetailAddress);
        rgTag = findViewById(R.id.rgTag);
        rbHome = findViewById(R.id.rbHome);
        rbCompany = findViewById(R.id.rbCompany);
        rbSchool = findViewById(R.id.rbSchool);
        cbDefault = findViewById(R.id.cbDefault);
        btnSave = findViewById(R.id.btnSave);
        
        // 设置工具栏
        setSupportActionBar(toolbar);
        toolbar.setTitle(addressId == -1 ? "添加地址" : "编辑地址");
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupSpinners() {
        // 省份
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, provinces);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);
        
        // 省份选择监听
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCitySpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // 城市选择监听
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDistrictSpinner(spinnerProvince.getSelectedItemPosition(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // 初始化城市和区县
        updateCitySpinner(0);
    }
    
    private void updateCitySpinner(int provinceIndex) {
        String[] cityArray = cities[provinceIndex];
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cityArray);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);
    }
    
    private void updateDistrictSpinner(int provinceIndex, int cityIndex) {
        String[] districtArray = districts[provinceIndex][cityIndex];
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, districtArray);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);
    }
    
    private void setListeners() {
        btnSave.setOnClickListener(v -> saveAddress());
    }
    
    private void loadAddressData() {
        editingAddress = database.addressDao().getAddressById(addressId);
        if (editingAddress == null) {
            Toast.makeText(this, "地址不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // 填充数据
        etReceiverName.setText(editingAddress.getReceiverName());
        etReceiverPhone.setText(editingAddress.getReceiverPhone());
        etDetailAddress.setText(editingAddress.getDetailAddress());
        cbDefault.setChecked(editingAddress.isDefault());
        
        // 设置省市区
        String province = editingAddress.getProvince();
        String city = editingAddress.getCity();
        String district = editingAddress.getDistrict();
        
        // 查找并设置省份
        for (int i = 0; i < provinces.length; i++) {
            if (provinces[i].equals(province)) {
                spinnerProvince.setSelection(i);
                
                // 设置城市
                String[] cityArray = cities[i];
                for (int j = 0; j < cityArray.length; j++) {
                    if (cityArray[j].equals(city)) {
                        spinnerCity.setSelection(j);
                        
                        // 设置区县
                        String[] districtArray = districts[i][j];
                        for (int k = 0; k < districtArray.length; k++) {
                            if (districtArray[k].equals(district)) {
                                spinnerDistrict.setSelection(k);
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        
        // 设置标签
        String tag = editingAddress.getTag();
        if ("家".equals(tag)) {
            rbHome.setChecked(true);
        } else if ("公司".equals(tag)) {
            rbCompany.setChecked(true);
        } else if ("学校".equals(tag)) {
            rbSchool.setChecked(true);
        }
    }
    
    private void saveAddress() {
        // 获取输入内容
        String receiverName = etReceiverName.getText().toString().trim();
        String receiverPhone = etReceiverPhone.getText().toString().trim();
        String province = spinnerProvince.getSelectedItem().toString();
        String city = spinnerCity.getSelectedItem().toString();
        String district = spinnerDistrict.getSelectedItem().toString();
        String detailAddress = etDetailAddress.getText().toString().trim();
        boolean isDefault = cbDefault.isChecked();
        
        // 获取标签
        String tag = "家";
        int checkedId = rgTag.getCheckedRadioButtonId();
        if (checkedId == R.id.rbHome) {
            tag = "家";
        } else if (checkedId == R.id.rbCompany) {
            tag = "公司";
        } else if (checkedId == R.id.rbSchool) {
            tag = "学校";
        }
        
        // 输入验证
        if (!validateInput(receiverName, receiverPhone, detailAddress)) {
            return;
        }
        
        // 创建或更新地址
        Address address;
        if (addressId == -1) {
            // 新增模式
            address = new Address();
            address.setUserId(currentUserId);
        } else {
            // 编辑模式
            address = editingAddress;
        }
        
        address.setReceiverName(receiverName);
        address.setReceiverPhone(receiverPhone);
        address.setProvince(province);
        address.setCity(city);
        address.setDistrict(district);
        address.setDetailAddress(detailAddress);
        address.setTag(tag);
        address.setDefault(isDefault);
        
        // 如果设为默认，先清除其他默认地址
        if (isDefault) {
            database.addressDao().clearDefaultAddress(currentUserId);
        }
        
        // 保存到数据库
        if (addressId == -1) {
            long id = database.addressDao().insert(address);
            if (id > 0) {
                SystemLogHelper.logAddressAction(this, currentUserId, "ADD", (int) id);
                Toast.makeText(this, "地址已添加", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
            }
        } else {
            database.addressDao().update(address);
            SystemLogHelper.logAddressAction(this, currentUserId, "UPDATE", addressId);
            Toast.makeText(this, "地址已更新", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }
    
    private boolean validateInput(String receiverName, String receiverPhone, String detailAddress) {
        if (TextUtils.isEmpty(receiverName)) {
            Toast.makeText(this, "请输入收件人姓名", Toast.LENGTH_SHORT).show();
            etReceiverName.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(receiverPhone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            etReceiverPhone.requestFocus();
            return false;
        }
        
        if (!PHONE_PATTERN.matcher(receiverPhone).matches()) {
            Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
            etReceiverPhone.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(detailAddress)) {
            Toast.makeText(this, "请输入详细地址", Toast.LENGTH_SHORT).show();
            etDetailAddress.requestFocus();
            return false;
        }
        
        return true;
    }
}