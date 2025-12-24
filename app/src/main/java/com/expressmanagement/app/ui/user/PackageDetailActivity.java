package com.expressmanagement.app.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expressmanagement.app.R;
import com.expressmanagement.app.database.AppDatabase;
import com.expressmanagement.app.entity.Address;
import com.expressmanagement.app.entity.LogisticsInfo;
import com.expressmanagement.app.entity.Package;
import com.expressmanagement.app.ui.user.adapter.LogisticsAdapter;
import com.expressmanagement.app.utils.PriceCalculator;
import com.expressmanagement.app.utils.TimeUtil;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

/**
 * 快递详情Activity
 */
public class PackageDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvStatus;
    private TextView tvLatestLogistics;
    private TextView tvTrackingNo;
    private TextView tvReceiverName;
    private TextView tvReceiverPhone;
    private TextView tvAddress;
    private TextView tvItemType;
    private TextView tvWeight;
    private TextView tvPrice;
    private LinearLayout layoutRemark;
    private TextView tvRemark;
    private RecyclerView recyclerViewLogistics;
    
    private AppDatabase database;
    private Package currentPackage;
    private LogisticsAdapter logisticsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_detail);
        
        database = AppDatabase.getInstance(this);
        
        // 获取包裹ID
        int packageId = getIntent().getIntExtra("package_id", -1);
        if (packageId == -1) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // 加载包裹数据
        currentPackage = database.packageDao().getPackageById(packageId);
        if (currentPackage == null) {
            Toast.makeText(this, "快递不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        loadPackageInfo();
        setupLogisticsRecyclerView();
        loadLogistics();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvStatus = findViewById(R.id.tvStatus);
        tvLatestLogistics = findViewById(R.id.tvLatestLogistics);
        tvTrackingNo = findViewById(R.id.tvTrackingNo);
        tvReceiverName = findViewById(R.id.tvReceiverName);
        tvReceiverPhone = findViewById(R.id.tvReceiverPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvItemType = findViewById(R.id.tvItemType);
        tvWeight = findViewById(R.id.tvWeight);
        tvPrice = findViewById(R.id.tvPrice);
        layoutRemark = findViewById(R.id.layoutRemark);
        tvRemark = findViewById(R.id.tvRemark);
        recyclerViewLogistics = findViewById(R.id.recyclerViewLogistics);
        
        // 设置工具栏
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void loadPackageInfo() {
        // 状态
        tvStatus.setText(currentPackage.getStatusName());
        
        // 运单号
        tvTrackingNo.setText("运单号：" + currentPackage.getTrackingNo());
        
        // 收件人信息
        tvReceiverName.setText(currentPackage.getReceiverName());
        tvReceiverPhone.setText(currentPackage.getReceiverPhone());
        
        // 地址
        Address address = database.addressDao().getAddressById(currentPackage.getAddressId());
        if (address != null) {
            tvAddress.setText(address.getFullAddress());
        }
        
        // 物品信息
        tvItemType.setText(currentPackage.getItemType());
        tvWeight.setText(currentPackage.getWeight() + "kg");
        tvPrice.setText("¥" + PriceCalculator.formatPrice(currentPackage.getPrice()));
        
        // 备注
        String remark = currentPackage.getRemark();
        if (!TextUtils.isEmpty(remark)) {
            layoutRemark.setVisibility(View.VISIBLE);
            tvRemark.setText(remark);
        } else {
            layoutRemark.setVisibility(View.GONE);
        }
    }
    
    private void setupLogisticsRecyclerView() {
        recyclerViewLogistics.setLayoutManager(new LinearLayoutManager(this));
        logisticsAdapter = new LogisticsAdapter(this);
        recyclerViewLogistics.setAdapter(logisticsAdapter);
    }
    
    private void loadLogistics() {
        List<LogisticsInfo> logisticsList = database.logisticsInfoDao()
                .getLogisticsByPackageId(currentPackage.getPid());
        
        if (!logisticsList.isEmpty()) {
            // 最新的物流信息
            LogisticsInfo latest = logisticsList.get(0);
            tvLatestLogistics.setText(latest.getInfo());
            
            // 设置物流轨迹列表
            logisticsAdapter.setLogisticsList(logisticsList);
        } else {
            tvLatestLogistics.setText("暂无物流信息");
        }
    }
}